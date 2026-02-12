package com.example.navya.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
    darkColorScheme(
        primary = GreenPrimaryDark,
        onPrimary = GreenOnPrimaryDark,
        primaryContainer = GreenContainerDark,
        secondary = GreenPrimaryDark,
        tertiary = GreenTertiary,
        background = DarkBackground,
        surface = DarkSurface,
        onBackground = TextWhite,
        onSurface = TextWhite,
        surfaceVariant = DarkSurfaceVariant,
        onSurfaceVariant = TextGray
    )

private val LightColorScheme =
    lightColorScheme(
        primary = GreenPrimary,
        secondary = GreenSecondary,
        tertiary = GreenTertiary,
        background = LightBackground,
        surface = LightSurface,
        onPrimary = Color.White,
        onSecondary = Color.White,
        onTertiary = Color.White,
        onBackground = Color.Black,
        onSurface = Color.Black,
    )

@Composable
fun NavyaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}


