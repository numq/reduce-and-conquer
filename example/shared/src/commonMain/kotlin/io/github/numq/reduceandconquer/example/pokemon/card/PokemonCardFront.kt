package io.github.numq.reduceandconquer.example.pokemon.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import io.github.numq.reduceandconquer.example.pokemon.Pokemon

@Composable
fun PokemonCardFront(modifier: Modifier, pokemon: Pokemon, bitmap: ImageBitmap?) {
    PokemonCardCommon(modifier = modifier, pokemon = pokemon) {
        when (bitmap) {
            null -> Icon(Icons.Default.BrokenImage, pokemon.name, modifier = Modifier.fillMaxSize(.5f))

            else -> Image(bitmap, pokemon.name, modifier = Modifier.fillMaxSize())
        }
    }
}