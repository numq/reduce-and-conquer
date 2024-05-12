package pokedex.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import card.FlippableCard
import kotlinx.coroutines.flow.filter
import pokemon.Pokemon
import pokemon.card.PokemonCard

@Composable
fun PokedexGrid(
    modifier: Modifier,
    gridState: LazyGridState,
    maxAttributeValue: Int,
    cards: List<FlippableCard<Pokemon>>,
    loadMore: () -> Unit,
    flip: (FlippableCard<Pokemon>) -> Unit,
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

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        state = gridState,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cards, key = { card -> card.item.id }) { card ->
            PokemonCard(
                modifier = Modifier.aspectRatio(.75f).padding(8.dp),
                card = card,
                maxAttributeValue = maxAttributeValue,
                flip = {
                    flip(card)
                }
            )
        }
    }
}