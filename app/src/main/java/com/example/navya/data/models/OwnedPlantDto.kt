package com.example.navya.data.models

import kotlinx.serialization.Serializable

@Serializable
data class OwnedPlantDto(
    val id: String? = null,
    val user_id: String,
    val plant_id: String,
    val purchase_date: Long
)
