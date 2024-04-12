package pokedex.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.filter
import pokemon.Pokemon
import pokemon.PokemonCard

@Composable
fun PokedexGrid(
    modifier: Modifier,
    gridState: LazyGridState,
    maxAttributeValue: Int,
    pokemons: List<Pokemon>,
    loadMore: () -> Unit,
) {
    val shouldLoadMore by remember(gridState.layoutInfo.totalItemsCount) {
        derivedStateOf {
            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false
            lastVisibleItem.index >= gridState.layoutInfo.totalItemsCount - 1
        }
    }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore }.filter { it }.collect { loadMore() }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().padding(8.dp),
            state = gridState,
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(pokemons) { pokemon ->
                PokemonCard(
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                    pokemon = pokemon,
                    maxAttributeValue = maxAttributeValue
                )
            }
        }
    }
}