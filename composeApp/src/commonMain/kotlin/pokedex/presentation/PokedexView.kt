package pokedex.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import notification.Notification
import notification.NotificationError

@Composable
fun PokedexView(feature: PokedexFeature, gridState: LazyGridState) {
    val state by feature.state.collectAsState()
    val errors = feature.effect.filterIsInstance(PokedexEffect.Error::class).map { error ->
        Notification.Error(
            durationMillis = 3_000L,
            message = error.message
        )
    }

    LaunchedEffect(Unit) {
        feature.effect.collect { effect ->
            when (effect) {
                is PokedexEffect.ScrollToStart -> gridState.animateScrollToItem(0)

                is PokedexEffect.ResetScroll -> gridState.scrollToItem(0)

                else -> Unit
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background), floatingActionButton = {
        AnimatedVisibility(gridState.firstVisibleItemIndex > 0, enter = fadeIn(), exit = fadeOut()) {
            FloatingActionButton(onClick = {
                feature.performEffect(PokedexEffect.ScrollToStart())
            }, backgroundColor = MaterialTheme.colors.background) {
                Icon(Icons.Default.ArrowUpward, null)
            }
        }
    }, floatingActionButtonPosition = FabPosition.Center) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.BottomCenter) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                PokedexInteraction(modifier = Modifier.fillMaxWidth(),
                    interactionMode = state.interactionMode,
                    sort = state.sort,
                    filters = state.filters,
                    isFiltered = state.isFiltered,
                    selectedFilter = state.selectedFilter,
                    toggleFilterMode = {
                        feature.dispatchMessage(PokedexMessage.Filter.ToggleFilterMode)
                    },
                    selectFilter = { criteria ->
                        feature.dispatchMessage(PokedexMessage.Filter.SelectFilter(criteria))
                    },
                    updateFilter = { filter ->
                        feature.dispatchMessage(PokedexMessage.Filter.UpdateFilter(filter))
                    },
                    closeFilter = {
                        feature.dispatchMessage(PokedexMessage.Filter.CloseFilter)
                    },
                    resetFilters = {
                        feature.dispatchMessage(PokedexMessage.Filter.ResetFilters)
                    },
                    toggleSortMode = {
                        feature.dispatchMessage(PokedexMessage.Sort.ToggleSortMode)
                    },
                    sortPokemons = { sort ->
                        feature.dispatchMessage(PokedexMessage.Sort.SortPokemons(sort))
                    })
                AnimatedVisibility(
                    visible = state.maxAttributeValue != null && state.pokemons.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    state.maxAttributeValue?.let { maxAttributeValue ->
                        PokedexGrid(
                            modifier = Modifier.fillMaxSize(),
                            gridState = gridState,
                            maxAttributeValue = maxAttributeValue,
                            pokemons = state.pokemons,
                            loadMore = { feature.dispatchMessage(PokedexMessage.Pokemons.LoadMorePokemons) }
                        )
                    }
                }
            }
            AnimatedVisibility(visible = state.pokemons.isEmpty(), enter = fadeIn(), exit = fadeOut()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No Pokémon found", textAlign = TextAlign.Center)
                }
            }
            NotificationError(errors)
        }
    }
}