package io.github.numq.reduceandconquer.example.pokedex.presentation.sort

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.numq.reduceandconquer.example.pokedex.sort.PokedexSort
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import reduce_and_conquer.example.generated.resources.*

@OptIn(ExperimentalResourceApi::class, ExperimentalLayoutApi::class)
@Composable
fun SortInteraction(
    modifier: Modifier,
    sort: PokedexSort,
    sortPokemons: (PokedexSort) -> Unit,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.Center
    ) {
        PokedexSort.Criteria.entries.forEach { criteria ->
            when (criteria) {
                PokedexSort.Criteria.NAME -> Res.string.sort_name

                PokedexSort.Criteria.HP -> Res.string.sort_hp

                PokedexSort.Criteria.SPEED -> Res.string.sort_speed

                PokedexSort.Criteria.BASIC_ATTACK -> Res.string.sort_basic_attack

                PokedexSort.Criteria.BASIC_DEFENSE -> Res.string.sort_basic_defense

                PokedexSort.Criteria.SPECIAL_ATTACK -> Res.string.sort_special_attack

                PokedexSort.Criteria.SPECIAL_DEFENSE -> Res.string.sort_special_defense
            }.let { res ->
                if (criteria == sort.criteria) {
                    OutlinedButton(onClick = {
                        sortPokemons(sort.copy(isAscending = !sort.isAscending))
                    }) {
                        Text(stringResource(res), textAlign = TextAlign.Center)
                    }
                } else {
                    Button(onClick = {
                        sortPokemons(PokedexSort(criteria = criteria, isAscending = sort.isAscending))
                    }) {
                        Text(stringResource(res), textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}