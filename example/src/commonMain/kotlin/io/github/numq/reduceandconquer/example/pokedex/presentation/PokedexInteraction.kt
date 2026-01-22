package io.github.numq.reduceandconquer.example.pokedex.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import io.github.numq.reduceandconquer.example.pokedex.filter.PokedexFilter
import io.github.numq.reduceandconquer.example.pokedex.presentation.filter.FilterInteraction
import io.github.numq.reduceandconquer.example.pokedex.presentation.sort.SortInteraction
import io.github.numq.reduceandconquer.example.pokedex.sort.PokedexSort

@Composable
fun PokedexInteraction(
    modifier: Modifier,
    interactionMode: PokedexInteractionMode,
    sort: PokedexSort,
    filters: List<PokedexFilter>,
    selectedFilter: PokedexFilter?,
    toggleFilterMode: () -> Unit,
    updateFilter: (PokedexFilter) -> Unit,
    selectFilter: (PokedexFilter.Criteria) -> Unit,
    resetFilter: (PokedexFilter.Criteria) -> Unit,
    resetFilters: () -> Unit,
    closeFilter: () -> Unit,
    toggleSortMode: () -> Unit,
    sortPokemons: (PokedexSort) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    val (query, setQuery) = remember { mutableStateOf("") }

    val (focused, setFocused) = remember { mutableStateOf(false) }

    LaunchedEffect(query) {
        updateFilter(PokedexFilter.Name(query))
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = setQuery,
                modifier = Modifier.weight(1f).focusRequester(focusRequester).onFocusChanged { state ->
                    setFocused(state.isFocused)
                },
                singleLine = true,
                trailingIcon = {
                    Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                        IconButton(onClick = {
                            if (query.isNotBlank()) setQuery("") else if (focused) focusRequester.freeFocus()
                        }, enabled = query.isNotBlank() || focused) {
                            Icon(Icons.Default.Clear, null)
                        }
                    }
                })
            IconButton(
                onClick = toggleFilterMode,
                modifier = Modifier.alpha(if (interactionMode == PokedexInteractionMode.FILTER) .75f else 1f)
            ) {
                val isFilterActive = interactionMode == PokedexInteractionMode.FILTER

                val iconTint = if (isFilterActive) {
                    MaterialTheme.colors.primary
                } else {
                    MaterialTheme.colors.onBackground
                }

                when (interactionMode) {
                    PokedexInteractionMode.FILTER -> Icon(
                        imageVector = Icons.Outlined.FilterAlt, contentDescription = "Filter", tint = iconTint
                    )

                    else -> Icon(imageVector = Icons.Default.FilterAlt, contentDescription = "Filter", tint = iconTint)
                }
            }
            IconButton(onClick = toggleSortMode) {
                val isSortActive = interactionMode == PokedexInteractionMode.SORT

                val iconTint = if (isSortActive) {
                    MaterialTheme.colors.primary
                } else {
                    MaterialTheme.colors.onBackground
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort,
                    contentDescription = "Sort",
                    tint = iconTint,
                    modifier = Modifier.rotate(if (sort.isAscending) 180f else 0f)
                )
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth().zIndex(1f).padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            when (interactionMode) {
                PokedexInteractionMode.NONE -> Unit

                PokedexInteractionMode.FILTER -> FilterInteraction(
                    modifier = Modifier.fillMaxWidth(),
                    filters = filters,
                    selectedFilter = selectedFilter,
                    selectFilter = selectFilter,
                    updateFilter = updateFilter,
                    resetFilter = resetFilter,
                    closeFilter = closeFilter,
                    resetFilters = resetFilters
                )

                PokedexInteractionMode.SORT -> SortInteraction(
                    modifier = Modifier.fillMaxWidth(), sort = sort, sortPokemons = sortPokemons
                )
            }
        }
    }
}