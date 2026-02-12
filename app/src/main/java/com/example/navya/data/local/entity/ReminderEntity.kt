package com.example.navya.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
        @PrimaryKey val id: String,
        val user_id: String,
        val plant_id: String,
        val interval_days: Int, // Added field
        val last_watered_at: Long?,
        val next_reminder_at: Long?
)
