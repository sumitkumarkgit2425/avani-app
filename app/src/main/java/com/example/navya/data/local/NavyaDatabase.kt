package com.example.navya.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.navya.data.local.dao.CartDao
import com.example.navya.data.local.dao.ReminderDao
import com.example.navya.data.local.entity.CartItemEntity
import com.example.navya.data.local.entity.ReminderEntity
import com.example.navya.data.local.entity.PlantEntity
import com.example.navya.data.local.entity.OwnedPlantEntity
import com.example.navya.data.local.entity.UserEntity

@Database(entities = [ReminderEntity::class, CartItemEntity::class, PlantEntity::class, OwnedPlantEntity::class, UserEntity::class], version = 6, exportSchema = false)
abstract class NavyaDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
    abstract fun cartDao(): CartDao
    abstract fun plantDao(): com.example.navya.data.local.dao.PlantDao
    abstract fun ownedPlantDao(): com.example.navya.data.local.dao.OwnedPlantDao
    abstract fun userDao(): com.example.navya.data.local.dao.UserDao
}
