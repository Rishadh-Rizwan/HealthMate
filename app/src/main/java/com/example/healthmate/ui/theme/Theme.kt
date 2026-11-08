package com.example.healthmate.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(

    primary = PrimaryContainer,
    onPrimary = TextPrimary,
    primaryContainer = Primary,
    onPrimaryContainer = Color.White,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE1E3E2),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE1E3E2),
    surfaceVariant = Color(0xFFE1E3E2),
    onSurfaceVariant = TextMuted,
    error = Error,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(

    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = TextPrimary,
    background = Background,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = Background,
    onSurfaceVariant = TextMuted,
    error = Error,
    onError = Color.White
)

@Composable
fun HealthMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode){
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =!darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}