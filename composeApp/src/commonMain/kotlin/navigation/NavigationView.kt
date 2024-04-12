package navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Today
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import daily.DailyView
import org.koin.compose.koinInject
import pokedex.presentation.PokedexView

@Composable
fun NavigationView(feature: NavigationFeature) {
    val state by feature.state.collectAsState()

    val pokedexGridState = rememberLazyGridState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            this@Column.AnimatedVisibility(visible = state.destination == Destination.DAILY,
                enter = slideInHorizontally { -it },
                exit = slideOutHorizontally { -it }) {
                DailyView(feature = koinInject())
            }
            this@Column.AnimatedVisibility(visible = state.destination == Destination.POKEDEX,
                enter = slideInHorizontally { it },
                exit = slideOutHorizontally { it }) {
                PokedexView(feature = koinInject(), gridState = pokedexGridState)
            }
        }
        BottomNavigation(modifier = Modifier.fillMaxWidth()) {
            BottomNavigationItem(selected = state.destination == Destination.DAILY, onClick = {
                feature.dispatchMessage(NavigationMessage.NavigateTo(Destination.DAILY))
            }, icon = {
                Icon(Icons.Default.Today, null)
            })
            BottomNavigationItem(selected = state.destination == Destination.POKEDEX, onClick = {
                feature.dispatchMessage(NavigationMessage.NavigateTo(Destination.POKEDEX))
            }, icon = {
                Icon(Icons.Default.GridView, null)
            })
        }
    }
}