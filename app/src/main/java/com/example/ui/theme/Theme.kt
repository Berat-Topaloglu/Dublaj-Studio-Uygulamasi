package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = StudioPrimary,
    secondary = StudioCardBg,
    tertiary = StudioAccent,
    background = StudioSecondary,
    surface = StudioSurfaceBg,
    onPrimary = Color.White,
    onSecondary = WhiteText,
    onTertiary = Color.Black,
    onBackground = WhiteText,
    onSurface = WhiteText
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Her zaman stüdyo koyu modunu kullan
    dynamicColor: Boolean = false, // Her zaman özel stüdyo renklerimizi koru
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
