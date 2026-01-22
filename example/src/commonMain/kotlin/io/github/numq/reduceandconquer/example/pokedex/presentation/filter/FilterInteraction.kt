package io.github.numq.reduceandconquer.example.pokedex.presentation.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.numq.reduceandconquer.example.pokedex.filter.PokedexFilter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import reduce_and_conquer.example.generated.resources.*

@OptIn(ExperimentalResourceApi::class)
@Composable
fun FilterInteraction(
    modifier: Modifier,
    filters: List<PokedexFilter>,
    selectedFilter: PokedexFilter?,
    selectFilter: (PokedexFilter.Criteria) -> Unit,
    resetFilter: (PokedexFilter.Criteria) -> Unit,
    updateFilter: (PokedexFilter) -> Unit,
    closeFilter: () -> Unit,
    resetFilters: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    selectedFilter?.let { filter ->
                        resetFilter(filter.criteria)
                    } ?: resetFilters()
                },
                enabled = selectedFilter?.isModified() ?: filters.any(PokedexFilter::isModified),
                modifier = Modifier.weight(.5f)
            ) {
                Text(
                    stringResource(Res.string.filter_reset).plus(" ").plus(
                        when (selectedFilter?.criteria) {
                            PokedexFilter.Criteria.TYPE -> stringResource(Res.string.filter_type)

                            PokedexFilter.Criteria.HP -> stringResource(Res.string.filter_hp)

                            PokedexFilter.Criteria.SPEED -> stringResource(Res.string.filter_speed)

                            PokedexFilter.Criteria.BASIC_ATTACK -> stringResource(Res.string.filter_basic_attack)

                            PokedexFilter.Criteria.BASIC_DEFENSE -> stringResource(Res.string.filter_basic_defense)

                            PokedexFilter.Criteria.SPECIAL_ATTACK -> stringResource(Res.string.filter_special_attack)

                            PokedexFilter.Criteria.SPECIAL_DEFENSE -> stringResource(Res.string.filter_special_defense)

                            else -> " "
                        }.lowercase().trim()
                    ), fontWeight = FontWeight.SemiBold
                )
            }
            if (selectedFilter != null) {
                Button(onClick = { closeFilter() }, modifier = Modifier.weight(.5f)) {
                    Text(stringResource(Res.string.filter_cancel), fontWeight = FontWeight.SemiBold)
                }
            }
        }
        selectedFilter?.let { filter ->
            SelectedFilter(
                modifier = Modifier.fillMaxWidth(), filter = filter, updateFilter = updateFilter
            )
        } ?: FilterRow(
            modifier = Modifier.fillMaxWidth(), filters = filters, selectFilter = selectFilter
        )
    }
}