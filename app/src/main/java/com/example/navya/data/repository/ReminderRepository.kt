package com.example.navya.data.repository

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.navya.data.local.dao.ReminderDao
import com.example.navya.data.local.entity.ReminderEntity
import com.example.navya.data.models.toEntity
import com.example.navya.workers.WaterReminderWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    val demoModeState: Flow<Boolean>
    fun getReminder(plantId: String): Flow<ReminderEntity?>
    fun getRemindersForUser(userId: String): Flow<List<ReminderEntity>>
    suspend fun setReminder(plantId: String, plantName: String, userId: String, intervalDays: Int)
    suspend fun markAsWatered(reminder: ReminderEntity, plantName: String)
    suspend fun snoozeReminder(reminder: ReminderEntity, plantName: String, durationMillis: Long)
    suspend fun cancelReminder(plantId: String)

    fun setDemoMode(enabled: Boolean)
    suspend fun triggerReminderNow(plantId: String, plantName: String)
    suspend fun triggerAllRemindersNow()
    suspend fun syncReminders(userId: String)
}

class ReminderRepositoryImpl
@Inject
constructor(
        private val reminderDao: ReminderDao,
        @ApplicationContext private val context: Context,
        private val apiService: com.example.navya.data.remote.ApiService
) : ReminderRepository {

    private val _demoMode = kotlinx.coroutines.flow.MutableStateFlow(false)
    override val demoModeState: Flow<Boolean> = _demoMode

    private val workManager = WorkManager.getInstance(context)

    override fun setDemoMode(enabled: Boolean) {
        _demoMode.value = enabled
    }

    override fun getReminder(plantId: String): Flow<ReminderEntity?> {
        return reminderDao.getReminder(plantId)
    }

    override fun getRemindersForUser(userId: String): Flow<List<ReminderEntity>> {
        return reminderDao.getRemindersForUser(userId)
    }

    override suspend fun syncReminders(userId: String) {
        try {
            val remoteReminders = apiService.getReminders("eq.$userId")
            val entities = remoteReminders.map { it.toEntity() }
            reminderDao.insertAll(entities)
        } catch (e: Exception) {}
    }

    override suspend fun setReminder(
            plantId: String,
            plantName: String,
            userId: String,
            intervalDays: Int
    ) {
        val delayMillis =
                if (_demoMode.value) {
                    10_000L
                } else {
                    intervalDays * 24 * 60 * 60 * 1000L
                }

        val nextTime = System.currentTimeMillis() + delayMillis

        val existingReminder = reminderDao.getReminderSync(plantId)
        val reminderId = existingReminder?.id ?: "reminder_${userId}_${plantId}"
        val lastWatered = existingReminder?.last_watered_at ?: System.currentTimeMillis()

        val reminder =
                ReminderEntity(
                        id = reminderId,
                        user_id = userId,
                        plant_id = plantId,
                        interval_days = intervalDays,
                        last_watered_at = lastWatered,
                        next_reminder_at = nextTime
                )
        reminderDao.insertReminder(reminder)

        scheduleWorker(plantId, plantName, delayMillis)

        try {
            val dto =
                    com.example.navya.data.models.ReminderDto(
                            id = reminder.id,
                            user_id = userId,
                            plant_id = plantId,
                            interval_days = intervalDays,
                            last_watered_at = reminder.last_watered_at,
                            next_reminder_at = reminder.next_reminder_at
                    )
            if (existingReminder != null) {
                apiService.updateReminder("eq.${reminder.id}", dto)
            } else {
                apiService.addReminder(dto)
            }
        } catch (e: Exception) {}
    }

    override suspend fun markAsWatered(reminder: ReminderEntity, plantName: String) {
        val now = System.currentTimeMillis()

        val delayMillis =
                if (_demoMode.value) {
                    10_000L
                } else {
                    (reminder.interval_days ?: 7) * 24 * 60 * 60 * 1000L
                }

        val nextTime = now + delayMillis

        val updated = reminder.copy(last_watered_at = now, next_reminder_at = nextTime)
        reminderDao.insertReminder(updated)

        scheduleWorker(reminder.plant_id, plantName, delayMillis)

        try {
            val dto =
                    com.example.navya.data.models.ReminderDto(
                            id = updated.id,
                            user_id = updated.user_id,
                            plant_id = updated.plant_id,
                            interval_days = updated.interval_days,
                            last_watered_at = updated.last_watered_at,
                            next_reminder_at = updated.next_reminder_at
                    )
            apiService.updateReminder("eq.${updated.id}", dto)
        } catch (e: Exception) {}
    }

    override suspend fun snoozeReminder(
            reminder: ReminderEntity,
            plantName: String,
            durationMillis: Long
    ) {
        val nextTime = System.currentTimeMillis() + durationMillis
        val updated = reminder.copy(next_reminder_at = nextTime)
        reminderDao.insertReminder(updated)

        scheduleWorker(reminder.plant_id, plantName, durationMillis)
    }

    private fun scheduleWorker(plantId: String, plantName: String, delayMillis: Long) {
        val data = workDataOf("plant_name" to plantName)
        val workRequest =
                OneTimeWorkRequestBuilder<WaterReminderWorker>()
                        .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                        .setInputData(data)
                        .addTag("reminder_$plantId")
                        .build()
        workManager.enqueueUniqueWork("reminder_$plantId", ExistingWorkPolicy.REPLACE, workRequest)
    }

    override suspend fun cancelReminder(plantId: String) {
        reminderDao.deleteReminder(plantId)
        workManager.cancelUniqueWork("reminder_$plantId")
        try {
            apiService.deleteReminder("eq.$plantId")
        } catch (e: Exception) {}
    }

    override suspend fun triggerReminderNow(plantId: String, plantName: String) {
        scheduleWorker(plantId, plantName, 100L)
    }

    override suspend fun triggerAllRemindersNow() {}
}
