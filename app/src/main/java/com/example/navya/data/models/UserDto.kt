package com.example.navya.data.models

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val display_name: String,
    val photo_url: String? = null
)
