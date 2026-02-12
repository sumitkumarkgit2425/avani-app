package com.example.navya.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "plants")
data class PlantEntity(
        @PrimaryKey val id: String = UUID.randomUUID().toString(),
        val name: String,
        val species: String?,
        val min_lux: Int?,
        val max_lux: Int?,
        val water_interval_days: Int?,
        val image_url: String?,
        val description: String?,
        val price: Double?,
        val currency: String? = "INR",
        val soil: String? = "Well-draining",
        val difficulty: String? = "Easy",
        val category: String = "Indoor"
)
