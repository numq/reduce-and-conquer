package pokedex.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import pokedex.filter.PokedexFilter
import pokedex.presentation.filter.FilterInteraction
import pokedex.presentation.sort.SortInteraction
import pokedex.sort.PokedexSort

@Composable
fun PokedexInteraction(
    modifier: Modifier,
    interactionMode: PokedexInteractionMode,
    sort: PokedexSort,
    filters: List<PokedexFilter>,
    isFiltered: Boolean,
    selectedFilter: PokedexFilter?,
    toggleFilterMode: () -> Unit,
    selectFilter: (PokedexFilter.Criteria) -> Unit,
    updateFilter: (PokedexFilter) -> Unit,
    resetFilter: (PokedexFilter.Criteria) -> Unit,
    closeFilter: () -> Unit,
    resetFilters: () -> Unit,
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
            OutlinedTextField(value = query,
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
            IconButton(onClick = {
                toggleFilterMode()
            }) {
                Icon(Icons.Default.FilterAlt, null)
            }
            IconButton(onClick = {
                toggleSortMode()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.Sort, null, modifier = Modifier.rotate(if (sort.isAscending) 180f else 0f)
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
                    isFiltered = isFiltered,
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