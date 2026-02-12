package com.example.navya.di

import android.content.Context
import androidx.room.Room
import com.example.navya.data.local.NavyaDatabase
import com.example.navya.data.local.dao.ReminderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideNavyaDatabase(@ApplicationContext context: Context): NavyaDatabase {
        return Room.databaseBuilder(
            context,
            NavyaDatabase::class.java,
            "navya_database"
        ).addMigrations(object : androidx.room.migration.Migration(5, 6) {
            override fun migrate(database: androidx.sqlite.db.SupportSQLiteDatabase) {

                database.execSQL("ALTER TABLE users ADD COLUMN local_photo_path TEXT")
            }
        })
        .fallbackToDestructiveMigration()
         .build()
    }

    @Provides
    @Singleton
    fun provideReminderDao(database: NavyaDatabase): ReminderDao {
        return database.reminderDao()
    }

    @Provides
    @Singleton
    fun provideCartDao(database: NavyaDatabase): com.example.navya.data.local.dao.CartDao {
        return database.cartDao()
    }

    @Provides
    @Singleton
    fun providePlantDao(database: NavyaDatabase): com.example.navya.data.local.dao.PlantDao {
        return database.plantDao()
    }

    @Provides
    @Singleton
    fun provideOwnedPlantDao(database: NavyaDatabase): com.example.navya.data.local.dao.OwnedPlantDao {
        return database.ownedPlantDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: NavyaDatabase): com.example.navya.data.local.dao.UserDao {
        return database.userDao()
    }
}
