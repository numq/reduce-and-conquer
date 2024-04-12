package pokedex.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import slider.IntRangeSlider

@Composable
fun FilterByAttribute(
    filter: PokedexFilter.Attribute,
    updateFilter: (PokedexFilter.Attribute) -> Unit,
) {
    val (value, setValue) = remember(filter.modified) { mutableStateOf(filter.modified) }

    val (isChanged, setIsChanged) = remember { mutableStateOf(false) }

    LaunchedEffect(isChanged) {
        if (isChanged) {
            updateFilter(filter.copy(modified = value))
            setIsChanged(false)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${value.first}", textAlign = TextAlign.Center)
            Text("${value.last}", textAlign = TextAlign.Center)
        }
        IntRangeSlider(
            value = value,
            onValueChange = setValue,
            modifier = Modifier.fillMaxWidth(),
            onValueChangeFinished = { setIsChanged(true) },
            valueRange = filter.default
        )
    }
}