package com.example.navya.data.models

import kotlinx.serialization.Serializable
import com.example.navya.data.local.entity.ReminderEntity

@Serializable
data class ReminderDto(
    val id: String,
    val user_id: String,
    val plant_id: String,
    val location: String? = null,
    val interval_days: Int? = null,
    val last_watered_at: Long?,
    val next_reminder_at: Long?
)

fun ReminderDto.toEntity(): ReminderEntity {
    return ReminderEntity(
        id = this.id,
        user_id = this.user_id,
        plant_id = this.plant_id,
        interval_days = this.interval_days ?: 7,
        last_watered_at = this.last_watered_at,
        next_reminder_at = this.next_reminder_at
    )
}
