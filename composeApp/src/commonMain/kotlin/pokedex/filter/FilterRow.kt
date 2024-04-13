package pokedex.filter

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
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import reduce_and_conquer.composeapp.generated.resources.*

@OptIn(ExperimentalResourceApi::class, ExperimentalLayoutApi::class)
@Composable
fun FilterRow(
    modifier: Modifier,
    filters: List<PokedexFilter>,
    selectFilter: (PokedexFilter.Criteria) -> Unit,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.Center
    ) {
        filters.filterNot { filter -> filter.criteria == PokedexFilter.Criteria.NAME }.forEach { filter ->
            when (filter.criteria) {
                PokedexFilter.Criteria.TYPE -> Res.string.filter_type

                PokedexFilter.Criteria.HP -> Res.string.filter_hp

                PokedexFilter.Criteria.SPEED -> Res.string.filter_speed

                PokedexFilter.Criteria.BASIC_ATTACK -> Res.string.filter_basic_attack

                PokedexFilter.Criteria.BASIC_DEFENSE -> Res.string.filter_basic_defense

                PokedexFilter.Criteria.SPECIAL_ATTACK -> Res.string.filter_special_attack

                PokedexFilter.Criteria.SPECIAL_DEFENSE -> Res.string.filter_special_defense

                else -> null
            }?.let { res ->
                if (filter.isModified()) {
                    OutlinedButton(onClick = { selectFilter(filter.criteria) }) {
                        Text(stringResource(res), textAlign = TextAlign.Center)
                    }
                } else {
                    Button(onClick = { selectFilter(filter.criteria) }) {
                        Text(stringResource(res), textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}