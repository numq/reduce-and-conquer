package io.github.numq.reduceandconquer.example.pokedex.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.numq.reduceandconquer.example.card.FlippableCard
import io.github.numq.reduceandconquer.example.pokemon.Pokemon
import io.github.numq.reduceandconquer.example.pokemon.card.PokemonCard

@Composable
fun PokedexGrid(
    modifier: Modifier,
    gridState: LazyGridState,
    maxAttributeValue: Int,
    cards: List<FlippableCard<Pokemon>>,
    flip: (FlippableCard<Pokemon>) -> Unit,
) {
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
                })
        }
    }
}