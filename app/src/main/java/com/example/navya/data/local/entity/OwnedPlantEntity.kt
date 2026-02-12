package com.example.navya.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "owned_plants")
data class OwnedPlantEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val user_id: String,
    val plant_id: String,
    val purchase_date: Long = System.currentTimeMillis()
)
