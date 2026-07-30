package io.github.numq.reduceandconquer.example.pokemon.card

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.numq.reduceandconquer.example.pokemon.Pokemon

@Composable
fun PokemonCardCommon(modifier: Modifier, pokemon: Pokemon, content: @Composable (Pokemon) -> Unit) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = pokemon.name, fontSize = 24.sp)

        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center
        ) {
            content(pokemon)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            pokemon.types.forEach { type ->
                val backgroundColor = Color(type.color)

                val textColor = when {
                    backgroundColor.luminance() > .5f -> Color.Black

                    else -> Color.White
                }

                Card(colors = CardDefaults.cardColors(containerColor = backgroundColor)) {
                    Text(
                        text = type.name,
                        textAlign = TextAlign.Center,
                        color = textColor,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}