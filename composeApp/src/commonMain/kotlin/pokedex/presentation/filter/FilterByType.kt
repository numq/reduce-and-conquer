package pokedex.presentation.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pokedex.filter.PokedexFilter
import pokemon.Pokemon

@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
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
                selected = type in filter.modified,
                onClick = {
                    updateFilter(filter.copy(modified = filter.modified.let { types ->
                        if (type in types) types.minus(type)
                        else types.plus(type)
                    }))
                },
                colors = ChipDefaults.filterChipColors(backgroundColor = Color.White)
            ) {
                Text(
                    text = type.name,
                    color = Color(type.color),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}