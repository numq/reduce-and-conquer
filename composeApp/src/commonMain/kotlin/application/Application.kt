package application

import androidx.compose.runtime.Composable
import navigation.NavigationView
import org.koin.compose.koinInject
import theme.ApplicationTheme

@Composable
fun Application() {
    ApplicationTheme {
        NavigationView(feature = koinInject())
    }
}