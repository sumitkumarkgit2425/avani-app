package com.example.navya.di

import android.content.Context
import com.example.navya.data.sensor.LightSensorManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SensorModule {

    @Provides
    @Singleton
    fun provideLightSensorManager(
        @ApplicationContext context: Context
    ): LightSensorManager {
        return LightSensorManager(context)
    }
}
