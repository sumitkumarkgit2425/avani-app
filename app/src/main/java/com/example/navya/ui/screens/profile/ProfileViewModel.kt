package com.example.navya.ui.screens.profile

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.example.navya.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ProfileUiState(
        val displayName: String = "",
        val email: String = "",
        val photoUrl: Any? = null,
        val isLoading: Boolean = false,
        val message: String? = null,
        val isDarkTheme: Boolean = false,
        val isNotificationsEnabled: Boolean = true
)

@HiltViewModel
class ProfileViewModel
@Inject
constructor(
        private val userRepository: UserRepository,
        private val settingsRepository: com.example.navya.data.repository.SettingsRepository,
        private val application: android.app.Application
) : androidx.lifecycle.AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()

        viewModelScope.launch {
            launch {
                settingsRepository.isDarkTheme.collect { isDark ->
                    _uiState.update { it.copy(isDarkTheme = isDark ?: false) }
                }
            }

            launch {
                settingsRepository.notificationsEnabled.collect { isEnabled ->
                    _uiState.update { it.copy(isNotificationsEnabled = isEnabled) }
                }
            }
        }
    }

    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch { settingsRepository.setDarkTheme(isDark) }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setNotificationsEnabled(enabled)
            val msg = if (enabled) "Notifications Enabled" else "Notifications Disabled"
            _uiState.update { it.copy(message = msg) }
        }
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        if (user != null) {
            viewModelScope.launch {
                userRepository.getUserProfile(user.uid).collect { userDto ->
                    if (userDto != null) {
                        _uiState.update {
                            it.copy(
                                    displayName = userDto.display_name,
                                    email = userDto.email,
                                    photoUrl = userDto.photo_url,
                                    isLoading = false
                            )
                        }
                    } else {

                        _uiState.update {
                            it.copy(
                                    displayName = user.displayName ?: "User",
                                    email = user.email ?: "",
                                    photoUrl = user.photoUrl,
                                    isLoading = false
                            )
                        }
                    }
                }
            }
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun updateProfileImage(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, message = "Uploading photo...") }
            val user = auth.currentUser

            if (user != null) {
                try {
                    val currentName =
                            _uiState.value.displayName.ifEmpty { user.displayName ?: "User" }
                    val currentEmail = _uiState.value.email.ifEmpty { user.email ?: "" }

                    val publicUrl =
                            userRepository.uploadProfileImage(
                                    uri,
                                    user.uid,
                                    application,
                                    currentName,
                                    currentEmail
                            )
                    val profileUpdates =
                            UserProfileChangeRequest.Builder()
                                    .setPhotoUri(Uri.parse(publicUrl))
                                    .build()

                    user.updateProfile(profileUpdates).await()

                    _uiState.update {
                        it.copy(
                                isLoading = false,
                                message = "Photo uploaded successfully!",
                                photoUrl = Uri.parse(publicUrl)
                        )
                    }

                    _uiState.update {
                        it.copy(
                                isLoading = false,
                                message = "Photo uploaded successfully!",
                                photoUrl = Uri.parse(publicUrl)
                        )
                    }
                } catch (e: Exception) {

                    val errorMsg = e.message ?: "Unknown error"

                    _uiState.update {
                        it.copy(isLoading = false, message = "Upload Failed: $errorMsg")
                    }
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(message = null) }
    }

    fun logout() {
        auth.signOut()
        _uiState.update { ProfileUiState() }
    }
}
