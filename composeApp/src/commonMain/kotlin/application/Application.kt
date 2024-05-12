package application

import androidx.compose.runtime.Composable
import navigation.NavigationView
import theme.ApplicationTheme

@Composable
fun Application() {
    ApplicationTheme {
        NavigationView()
    }
}