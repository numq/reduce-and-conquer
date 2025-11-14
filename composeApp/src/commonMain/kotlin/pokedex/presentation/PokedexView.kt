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
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import notification.NotificationError
import notification.queue.rememberNotificationQueue

@Composable
internal fun PokedexView(feature: PokedexFeature, gridState: LazyGridState) {
    val coroutineScope = rememberCoroutineScope { Dispatchers.Default }

    val notificationQueue = rememberNotificationQueue()

    val state by feature.state.collectAsState()

    val event by feature.events.collectAsState(null)

    LaunchedEffect(feature) {
        feature.events.filterIsInstance(PokedexEvent.Error::class).collect { error ->
            notificationQueue.push(message = error.message, label = Icons.Default.ErrorOutline)
        }
    }

    LaunchedEffect(event) {
        when (event) {
            is PokedexEvent.ScrollToStart -> gridState.animateScrollToItem(0)

            is PokedexEvent.ResetScroll -> gridState.scrollToItem(0)

            else -> Unit
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background), floatingActionButton = {
        AnimatedVisibility(gridState.firstVisibleItemIndex > 0, enter = fadeIn(), exit = fadeOut()) {
            FloatingActionButton(onClick = {
                coroutineScope.launch {
                    feature.execute(PokedexCommand.Cards.ResetScroll)
                }
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
                        coroutineScope.launch {
                            feature.execute(PokedexCommand.Filter.ToggleFilterMode)
                        }
                    },
                    selectFilter = { criteria ->
                        coroutineScope.launch {
                            feature.execute(PokedexCommand.Filter.SelectFilter(criteria))
                        }
                    },
                    updateFilter = { filter ->
                        coroutineScope.launch {
                            feature.execute(PokedexCommand.Filter.UpdateFilter(filter))
                        }
                    },
                    resetFilter = { criteria ->
                        coroutineScope.launch {
                            feature.execute(PokedexCommand.Filter.ResetFilter(criteria))
                        }
                    },
                    closeFilter = {
                        coroutineScope.launch {
                            feature.execute(PokedexCommand.Filter.CloseFilter)
                        }
                    },
                    resetFilters = {
                        coroutineScope.launch {
                            feature.execute(PokedexCommand.Filter.ResetFilters)
                        }
                    },
                    toggleSortMode = {
                        coroutineScope.launch {
                            feature.execute(PokedexCommand.Sort.ToggleSortMode)
                        }
                    },
                    sortPokemons = { sort ->
                        coroutineScope.launch {
                            feature.execute(PokedexCommand.Sort.SortPokemons(sort))
                        }
                    })
                AnimatedVisibility(
                    visible = state.maxAttributeValue != null && state.cards.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    state.maxAttributeValue?.let { maxAttributeValue ->
                        PokedexGrid(modifier = Modifier.fillMaxSize().padding(8.dp),
                            gridState = gridState,
                            maxAttributeValue = maxAttributeValue,
                            cards = state.cards,
                            loadMore = {
                                coroutineScope.launch {
                                    feature.execute(PokedexCommand.Cards.LoadMoreCards)
                                }
                            },
                            flip = { card ->
                                coroutineScope.launch {
                                    feature.execute(PokedexCommand.Cards.FlipCard(card = card))
                                }
                            })
                    }
                }
            }
            AnimatedVisibility(visible = state.cards.isEmpty(), enter = fadeIn(), exit = fadeOut()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No Pokémon found", textAlign = TextAlign.Center)
                }
            }
            NotificationError(notificationQueue = notificationQueue)
        }
    }
}