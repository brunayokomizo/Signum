package com.signum.app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BluePrimaryLight,
    secondary = BlueSecondaryLight,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = Color.White,
    onBackground = TextLight,
    onSurface = TextLight,
    surfaceVariant = Color(0xFFE3EDF7),
    onSurfaceVariant = TextLight
)

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimaryDark,
    secondary = BlueSecondaryDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = Color(0xFF051329),
    onBackground = TextDark,
    onSurface = TextDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextDark
)

@Composable
fun SignumTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
