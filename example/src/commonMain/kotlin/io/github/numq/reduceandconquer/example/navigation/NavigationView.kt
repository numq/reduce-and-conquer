package io.github.numq.reduceandconquer.example.navigation

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.numq.reduceandconquer.example.daily.DailyView
import io.github.numq.reduceandconquer.example.pokedex.presentation.PokedexView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun NavigationView(feature: NavigationFeature) {
    val coroutineScope = rememberCoroutineScope { Dispatchers.Default }

    val state by feature.state.collectAsState()

    val pokedexGridState = rememberLazyGridState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            this@Column.AnimatedVisibility(
                visible = state is NavigationState.Daily,
                enter = slideInHorizontally { -it },
                exit = slideOutHorizontally { -it }) {
                DailyView(feature = koinInject())
            }
            this@Column.AnimatedVisibility(
                visible = state is NavigationState.Pokedex,
                enter = slideInHorizontally { it },
                exit = slideOutHorizontally { it }) {
                PokedexView(gridState = pokedexGridState, feature = koinInject())
            }
        }
        BottomNavigation(modifier = Modifier.fillMaxWidth()) {
            BottomNavigationItem(selected = state is NavigationState.Daily, onClick = {
                coroutineScope.launch {
                    feature.execute(NavigationCommand.NavigateToDaily)
                }
            }, icon = {
                Icon(Icons.Default.Today, null)
            })
            BottomNavigationItem(selected = state is NavigationState.Pokedex, onClick = {
                coroutineScope.launch {
                    feature.execute(NavigationCommand.NavigateToPokedex)
                }
            }, icon = {
                Icon(Icons.Default.GridView, null)
            })
        }
    }
}