package com.example.navya.data.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface SettingsRepository {

    val isDarkTheme: Flow<Boolean?>
    suspend fun setDarkTheme(isDark: Boolean)

    val notificationsEnabled: Flow<Boolean>
    suspend fun setNotificationsEnabled(enabled: Boolean)

    val isOnboardingCompleted: Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)
}

@Singleton
class SettingsRepositoryImpl @Inject constructor(@ApplicationContext private val context: Context) :
        SettingsRepository {

    private val prefs: SharedPreferences =
            context.getSharedPreferences("navya_settings", Context.MODE_PRIVATE)

    override val isDarkTheme: Flow<Boolean?> = callbackFlow {
        val listener =
                SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                    if (key == "is_dark_theme") {
                        if (sharedPreferences.contains("is_dark_theme")) {
                            trySend(sharedPreferences.getBoolean("is_dark_theme", false))
                        } else {
                            trySend(null)
                        }
                    }
                }

        if (prefs.contains("is_dark_theme")) {
            trySend(prefs.getBoolean("is_dark_theme", false))
        } else {
            trySend(null)
        }

        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    override val notificationsEnabled: Flow<Boolean> = callbackFlow {
        val listener =
                SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                    if (key == "notifications_enabled") {
                        trySend(sharedPreferences.getBoolean("notifications_enabled", true))
                    }
                }

        trySend(prefs.getBoolean("notifications_enabled", true))

        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    override suspend fun setDarkTheme(isDark: Boolean) {
        prefs.edit().putBoolean("is_dark_theme", isDark).apply()
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("notifications_enabled", enabled).apply()
    }

    override val isOnboardingCompleted: Flow<Boolean> = callbackFlow {
        val listener =
                SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                    if (key == "onboarding_completed") {
                        trySend(sharedPreferences.getBoolean("onboarding_completed", false))
                    }
                }

        trySend(prefs.getBoolean("onboarding_completed", false))

        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean("onboarding_completed", completed).apply()
    }
}
