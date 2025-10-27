package com.nathakusuma.neriva.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    secondary = Secondary,
    onSecondary = Color.White,
    tertiary = InfoColor,
    background = BackgroundLight,
    surface = BackgroundSecondary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = ErrorColor,
    onError = Color.White
)

@Composable
fun NerivaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}