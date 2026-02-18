package com.handy.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PrimaryColor = Color(0xFF1565C0)
private val SecondaryColor = Color(0xFF42A5F5)
private val TertiaryColor = Color(0xFFFFA726)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    tertiary = TertiaryColor,
)

private val DarkColorScheme = darkColorScheme(
    primary = SecondaryColor,
    secondary = PrimaryColor,
    tertiary = TertiaryColor,
)

@Composable
fun HandyTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
