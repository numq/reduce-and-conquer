package io.github.numq.reduceandconquer.example.slider

import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

private fun IntRange.toClosedFloatingPointRange(): ClosedFloatingPointRange<Float> =
    (start.toFloat()..endInclusive.toFloat())

private fun ClosedFloatingPointRange<Float>.toIntRange(): IntRange = (start.toInt()..endInclusive.toInt())

@Composable
fun IntRangeSlider(
    value: IntRange,
    onValueChange: (IntRange) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: IntRange,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColors = SliderDefaults.colors(),
) = RangeSlider(
    value = value.toClosedFloatingPointRange(),
    onValueChange = { floatRange -> onValueChange(floatRange.toIntRange()) },
    modifier = modifier,
    enabled = enabled,
    valueRange = valueRange.toClosedFloatingPointRange(),
    steps = steps,
    onValueChangeFinished = onValueChangeFinished,
    colors = colors
)