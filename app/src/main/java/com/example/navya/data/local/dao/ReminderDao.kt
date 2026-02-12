package com.example.navya.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.navya.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders WHERE plant_id = :plantId LIMIT 1")
    fun getReminder(plantId: String): Flow<ReminderEntity?>

    @Query("SELECT * FROM reminders WHERE plant_id = :plantId LIMIT 1")
    suspend fun getReminderSync(plantId: String): ReminderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity): Long

    @Query("DELETE FROM reminders WHERE plant_id = :plantId")
    suspend fun deleteReminder(plantId: String): Int

    @Query("SELECT * FROM reminders WHERE user_id = :userId")
    fun getRemindersForUser(userId: String): Flow<List<ReminderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(reminders: List<ReminderEntity>): List<Long>
}
