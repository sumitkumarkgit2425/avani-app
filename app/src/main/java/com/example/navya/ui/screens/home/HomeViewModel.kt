package com.example.navya.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navya.data.local.entity.PlantEntity
import com.example.navya.data.local.entity.ReminderEntity
import com.example.navya.data.repository.NavyaRepository
import com.example.navya.data.repository.ReminderRepository
import com.example.navya.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class HomeUiState(
        val featuredPlants: List<PlantEntity> = emptyList(),
        val filteredPlants: List<PlantEntity> = emptyList(),
        val searchQuery: String = "",
        val upcomingReminder: ReminderEntity? = null,
        val reminderPlantName: String? = null,
        val reminderTimeLabel: String? = null,
        val isLoading: Boolean = false,
        val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NavyaRepository,
    private val reminderRepository: ReminderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchFeaturedPlants()
        fetchUpcomingReminder()
    }

    private fun fetchFeaturedPlants() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            _uiState.value = _uiState.value.copy(isLoading = true)
            repository
                    .getPlants()
                    .catch { e ->
                        _uiState.value =
                                _uiState.value.copy(
                                        isLoading = false,
                                        error = e.localizedMessage ?: "Unknown error"
                                )
                    }
                    .collect { plants ->
                        _uiState.value =
                                _uiState.value.copy(
                                        isLoading = false,
                                        featuredPlants = plants,
                                        filteredPlants = plants
                                )
                    }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        val allPlants = _uiState.value.featuredPlants
        
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(filteredPlants = allPlants)
        } else {
            val filtered = allPlants.filter {
                it.name.contains(query, ignoreCase = true) ||
                (it.species?.contains(query, ignoreCase = true) == true)
            }
            _uiState.value = _uiState.value.copy(filteredPlants = filtered)
        }
    }
    private fun fetchUpcomingReminder() {
        viewModelScope.launch {
            try {
                val userId = authRepository.getCurrentUser()?.uid ?: "anonymous"

                reminderRepository.getRemindersForUser(userId).collect { reminders ->
                    val now = System.currentTimeMillis()
                    val upcoming = reminders
                        .filter { (it.next_reminder_at ?: 0) > now }
                        .minByOrNull { it.next_reminder_at ?: Long.MAX_VALUE }

                    if (upcoming != null) {
                        repository.getPlantById(upcoming.plant_id).collect { plant ->
                            if (plant != null) {
                                val diffMillis = (upcoming.next_reminder_at ?: 0) - now
                                val diffHours = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(diffMillis)
                                val diffDays = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(diffMillis)

                                val label = when {
                                    diffDays > 0 -> "In $diffDays day(s)"
                                    diffHours > 0 -> "In $diffHours hour(s)"
                                    else -> "Soon"
                                }

                                _uiState.value = _uiState.value.copy(
                                    upcomingReminder = upcoming,
                                    reminderPlantName = plant.name,
                                    reminderTimeLabel = label
                                )
                            }
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(upcomingReminder = null)
                    }
                }
            } catch (e: Exception) {
            }
        }
    }
}
