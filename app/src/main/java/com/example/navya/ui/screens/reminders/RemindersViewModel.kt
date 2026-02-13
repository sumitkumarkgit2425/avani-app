package com.example.navya.ui.screens.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navya.data.local.entity.ReminderEntity
import com.example.navya.data.repository.AuthRepository
import com.example.navya.data.repository.NavyaRepository
import com.example.navya.data.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Collections.emptyList
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class ReminderUiItem(
        val reminder: ReminderEntity,
        val plantName: String,
        val plantImage: String?,
        val snippet: String,
        val timeLabel: String,
        val isOverdue: Boolean
)

data class RemindersUiState(
        val overdue: List<ReminderUiItem> = emptyList(),
        val today: List<ReminderUiItem> = emptyList(),
        val upcoming: List<ReminderUiItem> = emptyList(),
        val isLoading: Boolean = false,
        val isDemoMode: Boolean = false,
        val message: String? = null
)

@HiltViewModel
class RemindersViewModel
@Inject
constructor(
        private val reminderRepository: ReminderRepository,
        private val navyaRepository: NavyaRepository,
        private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RemindersUiState())
    val uiState: StateFlow<RemindersUiState> = _uiState.asStateFlow()

    init {
        loadReminders()
    }

    private fun loadReminders() {
        val userId = authRepository.getCurrentUser()?.uid ?: return

        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                reminderRepository.syncReminders(userId)
            } catch (e: Exception) {}
        }

        viewModelScope.launch {
            combine(
                            reminderRepository.getRemindersForUser(userId),
                            navyaRepository.getPlants(),
                            reminderRepository.demoModeState
                    ) { reminders, plants, isDemoMode ->
                val now = System.currentTimeMillis()

                val items =
                        reminders.distinctBy { it.plant_id }.mapNotNull { reminder ->
                            val plant = plants.find { it.id == reminder.plant_id }
                            if (plant != null) {
                                val diffMillis = (reminder.next_reminder_at ?: 0) - now
                                val diffDays = TimeUnit.MILLISECONDS.toDays(diffMillis)

                                val isOverdue = diffMillis < 0

                                val timeLabel =
                                        when {
                                            isOverdue -> "Overdue"
                                            diffDays == 0L -> "Today"
                                            diffDays == 1L -> "Tomorrow"
                                            else -> "In $diffDays days"
                                        }

                                ReminderUiItem(
                                        reminder = reminder,
                                        plantName = plant.name,
                                        plantImage = plant.image_url,
                                        snippet =
                                                "Water: Every ${plant.water_interval_days ?: 7} days",
                                        timeLabel = timeLabel,
                                        isOverdue = isOverdue
                                )
                            } else null
                        }

                Triple(items, isDemoMode, false)
            }
                    .collect { (items, isDemoMode, _) ->
                        val now = System.currentTimeMillis()
                        val overdue = items.filter { it.isOverdue }
                        val today =
                                items.filter {
                                    !it.isOverdue &&
                                            (it.reminder.next_reminder_at
                                                    ?: 0) - now < TimeUnit.DAYS.toMillis(1)
                                }
                        val upcoming =
                                items.filter {
                                    !it.isOverdue &&
                                            (it.reminder.next_reminder_at
                                                    ?: 0) - now >= TimeUnit.DAYS.toMillis(1)
                                }

                        _uiState.value =
                                _uiState.value.copy(
                                        isLoading = false,
                                        overdue = overdue,
                                        today = today,
                                        upcoming = upcoming,
                                        isDemoMode = isDemoMode
                                )
                    }
        }
    }

    fun markWatered(item: ReminderUiItem) {
        viewModelScope.launch {
            try {
                reminderRepository.markAsWatered(item.reminder, item.plantName)
                _uiState.value = _uiState.value.copy(message = "Water logged for ${item.plantName}")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(message = "Error: ${e.message}")
            }
        }
    }

    fun snooze(item: ReminderUiItem, durationMillis: Long = TimeUnit.DAYS.toMillis(1)) {
        viewModelScope.launch {
            try {

                val finalDuration = if (_uiState.value.isDemoMode) 10_000L else durationMillis
                reminderRepository.snoozeReminder(item.reminder, item.plantName, finalDuration)
                _uiState.value = _uiState.value.copy(message = "Snoozed ${item.plantName}")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(message = "Error: ${e.message}")
            }
        }
    }

    fun cancelReminder(item: ReminderUiItem) {
        viewModelScope.launch {
            try {
                reminderRepository.cancelReminder(item.reminder.plant_id)
                _uiState.value =
                        _uiState.value.copy(message = "Reminder cancelled for ${item.plantName}")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(message = "Error: ${e.message}")
            }
        }
    }

    fun toggleDemoMode() {
        val newState = !_uiState.value.isDemoMode
        reminderRepository.setDemoMode(newState)
        _uiState.value =
                _uiState.value.copy(
                        message = if (newState) "Demo Mode ON (10s intervals)" else "Demo Mode OFF"
                )
    }

    fun triggerNextReminder() {

        val allItems = _uiState.value.overdue + _uiState.value.today + _uiState.value.upcoming
        val nextItem = allItems.sortedBy { it.reminder.next_reminder_at }.firstOrNull()

        if (nextItem != null) {
            viewModelScope.launch {
                reminderRepository.triggerReminderNow(
                        nextItem.reminder.plant_id,
                        nextItem.plantName
                )
                _uiState.value =
                        _uiState.value.copy(
                                message = "Triggered notification for ${nextItem.plantName}"
                        )
            }
        } else {
            _uiState.value = _uiState.value.copy(message = "No reminders found to trigger")
        }
    }

    fun triggerAllReminders() {
        val allItems = _uiState.value.overdue + _uiState.value.today + _uiState.value.upcoming
        viewModelScope.launch {
            allItems.forEach { item ->
                reminderRepository.triggerReminderNow(item.reminder.plant_id, item.plantName)
            }
            _uiState.value =
                    _uiState.value.copy(message = "Fired all ${allItems.size} reminders now!")
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}
