package io.github.numq.reduceandconquer.example.pokemon.card

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            content(pokemon)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            pokemon.types.forEach { type ->
                Card(backgroundColor = Color(type.color)) {
                    Text(
                        text = type.name,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f).padding(8.dp)
                    )
                }
            }
        }
    }
}