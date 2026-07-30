package io.github.numq.reduceandconquer.example.pokemon.chart

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.luminance
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

    val textColor = MaterialTheme.colorScheme.onSurface

    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < .5f

    val defaultColor = if (isDarkTheme) Color.White else Color.Black

    val animatedColor = remember(colors) { Animatable(if (colors.isEmpty()) defaultColor else Color.Unspecified) }

    LaunchedEffect(colors, isDarkTheme) {
        if (colors.isEmpty()) return@LaunchedEffect

        val targetColors = colors.map { rawColor ->
            val c = Color(rawColor)

            adjustColorForContrast(c, isDarkTheme)
        }

        animatedColor.snapTo(targetColors.first())

        if (targetColors.size > 1) {
            var index = 0

            while (isActive) {
                index = (index + 1) % targetColors.size

                animatedColor.animateTo(
                    targetValue = targetColors[index], animationSpec = tween(
                        durationMillis = 500, delayMillis = 500, easing = LinearEasing
                    )
                )
            }
        }
    }

    Canvas(modifier = modifier) {
        val size = (minOf(size.width, size.height) * 0.45f).let { Size(it, it) }

        val outerHexagonVertices = calculateHexagonVertices(
            values = List(items.size) { maxAttributeValue }, center = center, size = size, maxValue = maxAttributeValue
        )

        val innerHexagonVertices = calculateHexagonVertices(
            values = items.map(ChartItem::value), center = center, size = size, maxValue = maxAttributeValue
        )

        val chartColor = if (animatedColor.value == Color.Unspecified) defaultColor else animatedColor.value

        for (i in 0..<minOf(6, items.size)) {
            drawLine(
                start = center,
                end = outerHexagonVertices[i],
                alpha = .35f,
                color = chartColor,
                strokeWidth = 1.5f,
                cap = StrokeCap.Butt
            )

            val text = "${items[i].name}\n${items[i].value}"

            val measuredText =
                textMeasurer.measure(text = text, style = TextStyle(textAlign = TextAlign.Center, color = textColor))

            val textWidth = measuredText.size.width

            val textHeight = measuredText.size.height

            val textOffsetX = when {
                outerHexagonVertices[i].x < center.x -> -textWidth - 8f

                outerHexagonVertices[i].x == center.x -> -textWidth / 2f

                else -> 8f
            }
            val textOffsetY = when {
                outerHexagonVertices[i].y < center.y -> -textHeight - 4f

                outerHexagonVertices[i].y == center.y -> -textHeight / 2f

                else -> 4f
            }

            drawText(
                textLayoutResult = measuredText, topLeft = Offset(
                    outerHexagonVertices[i].x + textOffsetX, outerHexagonVertices[i].y + textOffsetY
                )
            )
        }

        drawPath(
            path = Path().apply {
                outerHexagonVertices.forEachIndexed { index, offset ->
                    when (index) {
                        0 -> moveTo(offset.x, offset.y)

                        else -> lineTo(offset.x, offset.y)
                    }
                }

                close()
            }, alpha = .5f, color = chartColor, style = Stroke(2f, cap = StrokeCap.Butt)
        )

        drawPath(
            path = Path().apply {
                innerHexagonVertices.forEachIndexed { index, offset ->
                    when (index) {
                        0 -> moveTo(offset.x, offset.y)

                        else -> lineTo(offset.x, offset.y)
                    }
                }

                close()
            },
            alpha = .35f,
            color = chartColor,
        )

        drawPath(
            path = Path().apply {
                innerHexagonVertices.forEachIndexed { index, offset ->
                    when (index) {
                        0 -> moveTo(offset.x, offset.y)

                        else -> lineTo(offset.x, offset.y)
                    }
                }

                close()
            }, alpha = .8f, color = chartColor, style = Stroke(2f, cap = StrokeCap.Butt)
        )
    }
}

private fun adjustColorForContrast(color: Color, isDarkTheme: Boolean): Color {
    val lum = color.luminance()

    return when {
        isDarkTheme && lum < 0.25f -> color.copy(
            red = minOf(1f, color.red + 0.35f),
            green = minOf(1f, color.green + 0.35f),
            blue = minOf(1f, color.blue + 0.35f)
        )

        !isDarkTheme && lum > 0.75f -> color.copy(
            red = maxOf(0f, color.red - 0.35f),
            green = maxOf(0f, color.green - 0.35f),
            blue = maxOf(0f, color.blue - 0.35f)
        )

        else -> color
    }
}

private fun calculateHexagonVertices(values: List<Int>, center: Offset, size: Size, maxValue: Int): List<Offset> {
    if (values.isEmpty() || maxValue == 0) return emptyList()

    val vertices = mutableListOf<Offset>()

    val radius = minOf(size.width, size.height) / 2

    val angleDeg = 360f / values.size

    val startAngle = -30f

    for (i in values.indices) {
        val angleRad = (startAngle + i * angleDeg).toDouble() / 180.0 * PI

        val factor = (values[i].toFloat() / maxValue).coerceIn(0f, 1f)

        val x = center.x + radius * cos(angleRad).toFloat() * factor

        val y = center.y + radius * sin(angleRad).toFloat() * factor

        vertices.add(Offset(x, y))
    }

    return vertices
}