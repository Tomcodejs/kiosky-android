package com.kiosky.launcher.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val KioskyColorScheme = darkColorScheme(
    primary = GlassHighlight,
    background = BackgroundBottom,
    surface = BackgroundTop
)

@Composable
fun KioskyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KioskyColorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
