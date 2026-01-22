package io.github.numq.reduceandconquer.example.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColors(
    primary = Color(0xFFEF5350),
    primaryVariant = Color(0xFFD32F2F),
    secondary = Color(0xFF1976D2),
    secondaryVariant = Color(0xFF0D47A1),
    background = Color.White,
    surface = Color.White,
    error = Color(0xFFB00020),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White
)

private val DarkColors = darkColors(
    primary = Color(0xFFEF5350),
    primaryVariant = Color(0xFFD32F2F),
    secondary = Color(0xFF1976D2),
    secondaryVariant = Color(0xFF0D47A1),
    background = Color(0xFF212121),
    surface = Color(0xFF121212),
    error = Color(0xFFB00020),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.White
)

@Composable
fun ApplicationTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content,
        colors = if (darkTheme) DarkColors else LightColors
    )
}