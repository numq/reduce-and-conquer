package pokedex.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

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

            is PokedexFilter.Type -> FilterByType(filter = filter, updateFilter = updateFilter)

            is PokedexFilter.Attribute -> FilterByAttribute(filter = filter, updateFilter = updateFilter)
        }
    }
}