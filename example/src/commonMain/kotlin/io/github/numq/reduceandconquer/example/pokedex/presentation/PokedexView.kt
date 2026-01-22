package io.github.numq.reduceandconquer.example.pokedex.presentation

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
import io.github.numq.reduceandconquer.example.notification.NotificationError
import io.github.numq.reduceandconquer.example.notification.queue.rememberNotificationQueue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
internal fun PokedexView(feature: PokedexFeature, gridState: LazyGridState) {
    val coroutineScope = rememberCoroutineScope { Dispatchers.Default }

    val notificationQueue = rememberNotificationQueue()

    val state by feature.state.collectAsState()

    LaunchedEffect(Unit) {
        feature.execute(PokedexCommand.Initialize)

        feature.events.collect { event ->
            when (event) {
                is PokedexEvent.Error -> notificationQueue.push(
                    message = event.message, label = Icons.Default.ErrorOutline
                )

                is PokedexEvent.ScrollToStart -> launch {
                    gridState.animateScrollToItem(0)
                }
            }
        }
    }

    LaunchedEffect(state.cards) {
        gridState.scrollToItem(0)
    }

    Scaffold(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background), floatingActionButton = {
        AnimatedVisibility(visible = gridState.firstVisibleItemIndex > 0, enter = fadeIn(), exit = fadeOut()) {
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        feature.execute(PokedexCommand.ScrollToStart)
                    }
                },
                backgroundColor = MaterialTheme.colors.background,
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp)
            ) {
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
                PokedexInteraction(
                    modifier = Modifier.fillMaxWidth(),
                    interactionMode = state.interactionMode,
                    sort = state.sort,
                    filters = state.filters,
                    selectedFilter = state.selectedFilter,
                    toggleFilterMode = {
                        coroutineScope.launch {
                            feature.execute(PokedexCommand.Filter.ToggleFilterMode)
                        }
                    },
                    updateFilter = { filter ->
                        coroutineScope.launch {
                            feature.execute(PokedexCommand.Filter.UpdateFilter(filter))
                        }
                    },
                    selectFilter = { criteria ->
                        coroutineScope.launch {
                            feature.execute(PokedexCommand.Filter.SelectFilter(criteria))
                        }
                    },
                    resetFilter = { criteria ->
                        coroutineScope.launch {
                            feature.execute(PokedexCommand.Filter.ResetFilter(criteria))
                        }
                    },
                    resetFilters = {
                        coroutineScope.launch {
                            feature.execute(PokedexCommand.Filter.ResetFilters)
                        }
                    },
                    closeFilter = {
                        coroutineScope.launch {
                            feature.execute(PokedexCommand.Filter.CloseFilter)
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
                        PokedexGrid(
                            modifier = Modifier.fillMaxSize().padding(8.dp),
                            gridState = gridState,
                            maxAttributeValue = maxAttributeValue,
                            cards = state.cards,
                            flip = { card ->
                                coroutineScope.launch {
                                    feature.execute(PokedexCommand.FlipCard(card = card))
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