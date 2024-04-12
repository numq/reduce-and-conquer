package pokedex.filter

import androidx.compose.foundation.layout.*
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
import pokemon.Pokemon

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FilterByType(
    filter: PokedexFilter.Type,
    updateFilter: (PokedexFilter.Type) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Pokemon.Type.entries.chunked(Pokemon.Type.entries.size / 3).forEach { chunk ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                chunk.forEach { type ->
                    FilterChip(
                        selected = type in filter.modified,
                        onClick = {
                            updateFilter(filter.copy(modified = filter.modified.let { types ->
                                if (type in types) types.minus(type)
                                else types.plus(type)
                            }))
                        },
                        colors = ChipDefaults.filterChipColors(backgroundColor = Color.White),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            type.name,
                            color = Color(type.color),
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f).padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}