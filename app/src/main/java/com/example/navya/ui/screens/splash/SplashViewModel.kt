package com.example.navya.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navya.data.repository.AuthRepository
import com.example.navya.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class SplashViewModel
@Inject
constructor(
        private val authRepository: AuthRepository,
        private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination = _startDestination.asStateFlow()

    init {
        determineStartDestination()
    }

    private fun determineStartDestination() {
        viewModelScope.launch {
            val onboardingCompleted = settingsRepository.isOnboardingCompleted.first()
            val user = authRepository.getCurrentUser()

            if (!onboardingCompleted) {
                _startDestination.value = "onboarding"
            } else if (user == null) {

                _startDestination.value = "login_screen"
            } else {

                _startDestination.value = "home_screen"
            }
        }
    }
}
