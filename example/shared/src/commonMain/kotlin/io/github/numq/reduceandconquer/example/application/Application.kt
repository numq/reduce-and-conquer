package io.github.numq.reduceandconquer.example.application

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import io.github.numq.reduceandconquer.example.navigation.NavigationView
import io.github.numq.reduceandconquer.example.theme.ApplicationTheme
import org.koin.compose.koinInject

@Composable
fun Application() {
    ApplicationTheme(darkTheme = isSystemInDarkTheme()) {
        NavigationView(feature = koinInject())
    }
}