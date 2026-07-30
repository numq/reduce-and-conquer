package io.github.numq.reduceandconquer.example.pokedex.presentation.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.numq.reduceandconquer.example.pokedex.filter.PokedexFilter
import io.github.numq.reduceandconquer.example.pokemon.Pokemon

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterByType(
    modifier: Modifier,
    filter: PokedexFilter.Type,
    updateFilter: (PokedexFilter.Type) -> Unit,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterVertically)
    ) {
        Pokemon.Type.entries.forEach { type ->
            FilterChip(
                selected = type in filter.modified, onClick = {
                    updateFilter(filter.copy(modified = filter.modified.let { types ->
                        when (type) {
                            in types -> types.minus(type)

                            else -> types.plus(type)
                        }
                    }))
                }, label = {
                    Text(
                        text = type.name, color = Color(type.color), fontSize = 16.sp, textAlign = TextAlign.Center
                    )
                }, colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    labelColor = Color(type.color),
                    selectedContainerColor = Color(type.color).copy(alpha = .2f)
                )
            )
        }
    }
}