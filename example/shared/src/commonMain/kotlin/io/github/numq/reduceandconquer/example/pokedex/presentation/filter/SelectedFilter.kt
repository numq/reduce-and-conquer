package io.github.numq.reduceandconquer.example.pokedex.presentation.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.numq.reduceandconquer.example.pokedex.filter.PokedexFilter

@Composable
fun SelectedFilter(
    modifier: Modifier,
    filter: PokedexFilter,
    updateFilter: (PokedexFilter) -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        when (filter) {
            is PokedexFilter.Name -> Unit

            is PokedexFilter.Type -> FilterByType(
                modifier = Modifier.fillMaxWidth(), filter = filter, updateFilter = updateFilter
            )

            is PokedexFilter.Attribute -> FilterByAttribute(
                modifier = Modifier.fillMaxWidth(), filter = filter, updateFilter = updateFilter
            )
        }
    }
}