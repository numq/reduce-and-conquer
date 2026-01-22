package io.github.numq.reduceandconquer.example.pokemon.chart

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PokemonAttributeChart(
    modifier: Modifier = Modifier,
    maxAttributeValue: Int,
    items: List<ChartItem>,
    colors: List<Long>,
) {
    val textMeasurer = rememberTextMeasurer()

    val animatedColor = remember(colors) { Animatable(if (colors.isEmpty()) Color.Black else Color.Unspecified) }

    LaunchedEffect(Unit) {
        if (colors.isNotEmpty()) {
            var index = 0
            while (isActive) {
                index = (index + 1) % colors.size
                (Color(colors[index])).let { color ->
                    if (animatedColor.value == Color.Unspecified) animatedColor.snapTo(color)
                    else animatedColor.animateTo(
                        color, animationSpec = tween(
                            durationMillis = 500, delayMillis = 500, easing = LinearEasing
                        )
                    )
                }
            }
        }
    }

    Canvas(modifier = modifier) {
        val size = (minOf(size.width, size.height) * 0.5f).let { Size(it, it) }

        val outerHexagonVertices = calculateHexagonVertices(
            values = List(items.size) { maxAttributeValue }, center = center, size = size, maxValue = maxAttributeValue
        )

        val innerHexagonVertices = calculateHexagonVertices(
            values = items.map(ChartItem::value), center = center, size = size, maxValue = maxAttributeValue
        )

        for (i in 0..<6) {
            drawLine(
                start = center,
                end = outerHexagonVertices[i],
                alpha = .5f,
                color = animatedColor.value,
                strokeWidth = 2f,
                cap = StrokeCap.Butt
            )

            val text = items[i].name + "\n" + items[i].value

            val textWidth = textMeasurer.measure(text).size.width
            val textHeight = textMeasurer.measure(text).size.height

            val textOffsetX = when {
                outerHexagonVertices[i].x < center.x -> -textWidth
                outerHexagonVertices[i].x == center.x -> -textWidth / 2
                else -> 0
            }
            val textOffsetY = when {
                outerHexagonVertices[i].y <= center.y -> -textHeight
                else -> 0
            }

            if (animatedColor.value != Color.Unspecified) drawText(
                textMeasurer = textMeasurer,
                text = items[i].name + "\n" + "${items[i].value}",
                style = TextStyle(textAlign = TextAlign.Center),
                topLeft = Offset(
                    outerHexagonVertices[i].x + textOffsetX, outerHexagonVertices[i].y + textOffsetY
                )
            )
        }
        drawPath(
            path = Path().apply {
                outerHexagonVertices.forEachIndexed { index, offset ->
                    if (index == 0) {
                        moveTo(offset.x, offset.y)
                    } else {
                        lineTo(offset.x, offset.y)
                    }
                }
                close()
            }, alpha = .5f, color = animatedColor.value, style = Stroke(2f, cap = StrokeCap.Butt)
        )
        drawPath(
            path = Path().apply {
                innerHexagonVertices.forEachIndexed { index, offset ->
                    if (index == 0) {
                        moveTo(offset.x, offset.y)
                    } else {
                        lineTo(offset.x, offset.y)
                    }
                }
                close()
            },
            alpha = .5f,
            color = animatedColor.value,
        )
    }
}

fun calculateHexagonVertices(values: List<Int>, center: Offset, size: Size, maxValue: Int): List<Offset> {
    val vertices = mutableListOf<Offset>()
    val radius = minOf(size.width, size.height) / 2
    val angleDeg = 360f / values.size
    val startAngle = -30f
    for (i in values.indices) {
        val angleRad = (startAngle + i * angleDeg).toDouble() / 180.0 * PI
        val x = center.x + radius * cos(angleRad).toFloat() * values[i] / maxValue
        val y = center.y + radius * sin(angleRad).toFloat() * values[i] / maxValue
        vertices.add(Offset(x, y))
    }
    return vertices
}