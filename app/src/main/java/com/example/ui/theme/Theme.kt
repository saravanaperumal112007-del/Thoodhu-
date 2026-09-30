package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SkyPrimaryDark,
    onPrimary = SlateDark,
    primaryContainer = SkyPrimaryContainer,
    onPrimaryContainer = Color(0xFFE0F2FE),
    secondary = SkySecondaryDark,
    onSecondary = SlateDark,
    tertiary = EagleGoldLight,
    onTertiary = SlateDark,
    background = MidnightNavy,
    onBackground = CloudWhite,
    surface = SlateDark,
    onSurface = CloudWhite,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = OutlineDark,
    outlineVariant = Color(0xFF334155),
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A)
)

private val LightColorScheme = lightColorScheme(
    primary = SkyPrimary,
    onPrimary = SkyOnPrimary,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = SkySecondary,
    onSecondary = Color.White,
    tertiary = EagleGold,
    onTertiary = Color.White,
    background = CloudWhite,
    onBackground = Color(0xFF0F172A),
    surface = SurfaceLight,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = SurfaceLightVariant,
    onSurfaceVariant = Color(0xFF475569),
    outline = OutlineLight,
    outlineVariant = Color(0xFFE2E8F0),
    error = Color(0xFFDC2626),
    onError = Color.White
)

@Composable
fun ThoodhuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve brand identity by default
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
