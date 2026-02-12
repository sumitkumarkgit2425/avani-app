package com.example.navya.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val display_name: String,
    val photo_url: String? = null,
    val local_photo_path: String? = null
)
