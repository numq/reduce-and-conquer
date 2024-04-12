package pokedex.sort

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import reduce_and_conquer.composeapp.generated.resources.*

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SortInteraction(
    modifier: Modifier,
    sort: PokedexSort,
    sortPokemons: (PokedexSort) -> Unit,
) {
    val rows = remember {
        listOf(
            listOf(
                PokedexSort.Criteria.NAME,
                PokedexSort.Criteria.HP,
                PokedexSort.Criteria.SPEED
            ), listOf(
                PokedexSort.Criteria.BASIC_ATTACK,
                PokedexSort.Criteria.BASIC_DEFENSE,
                PokedexSort.Criteria.SPECIAL_ATTACK,
                PokedexSort.Criteria.SPECIAL_DEFENSE
            )
        )
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                row.forEach { criteria ->
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
                            }, modifier = Modifier.weight(1f)) {
                                Text(stringResource(res), textAlign = TextAlign.Center)
                            }
                        } else {
                            Button(onClick = {
                                sortPokemons(PokedexSort(criteria = criteria, isAscending = sort.isAscending))
                            }, modifier = Modifier.weight(1f)) {
                                Text(stringResource(res), textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }
    }
}