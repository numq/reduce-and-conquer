package pokedex.filter

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
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import reduce_and_conquer.composeapp.generated.resources.Res
import reduce_and_conquer.composeapp.generated.resources.filter_cancel
import reduce_and_conquer.composeapp.generated.resources.filter_reset

@OptIn(ExperimentalResourceApi::class)
@Composable
fun FilterInteraction(
    modifier: Modifier,
    filters: List<PokedexFilter>,
    isFiltered: Boolean,
    selectedFilter: PokedexFilter?,
    selectFilter: (PokedexFilter.Criteria) -> Unit,
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
                onClick = { resetFilters() },
                enabled = isFiltered,
                modifier = Modifier.weight(.5f)
            ) {
                Text(stringResource(Res.string.filter_reset), fontWeight = FontWeight.SemiBold)
            }
            if (selectedFilter != null) {
                Button(onClick = { closeFilter() }, modifier = Modifier.weight(.5f)) {
                    Text(stringResource(Res.string.filter_cancel), fontWeight = FontWeight.SemiBold)
                }
            }
        }
        selectedFilter?.let { filter ->
            SelectedFilter(
                modifier = Modifier.fillMaxWidth(),
                filter = filter,
                updateFilter = updateFilter
            )
        } ?: FilterRow(
            modifier = Modifier.fillMaxWidth(),
            filters = filters,
            selectFilter = selectFilter
        )
    }
}