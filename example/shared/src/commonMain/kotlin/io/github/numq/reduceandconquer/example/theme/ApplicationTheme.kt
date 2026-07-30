package io.github.numq.reduceandconquer.example.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PokedexRed = Color(0xFFDC0A2D)

private val PokedexRedDark = Color(0xFF8B0000)

private val PokedexBlueLens = Color(0xFF28AAFD)

private val PokedexBlueContainer = Color(0xFF00497D)

private val PokedexYellow = Color(0xFFFFCB05)

private val LightColorScheme = lightColorScheme(
    primary = PokedexRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF410002),
    secondary = PokedexBlueLens,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD0E4FF),
    onSecondaryContainer = Color(0xFF001D36),
    tertiary = PokedexYellow,
    onTertiary = Color.Black,
    background = Color(0xFFF7F7F7),
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    surfaceContainer = Color(0xFFF3EDF7),
    error = Color(0xFFB00020),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB4AB),
    onPrimary = Color(0xFF690005),
    primaryContainer = PokedexRedDark,
    onPrimaryContainer = Color(0xFFFFDAD6),
    secondary = PokedexBlueLens,
    onSecondary = Color(0xFF003258),
    secondaryContainer = PokedexBlueContainer,
    onSecondaryContainer = Color(0xFFD0E4FF),
    tertiary = PokedexYellow,
    onTertiary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF2B2B2B),
    onSurfaceVariant = Color(0xFFCAC4D0),
    surfaceContainer = Color(0xFF242424),
    error = Color(0xFFCF6679),
    onError = Color.Black
)

@Composable
fun ApplicationTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        typography = MaterialTheme.typography, shapes = MaterialTheme.shapes, content = content, colorScheme = when {
            darkTheme -> DarkColorScheme

            else -> LightColorScheme
        }
    )
}