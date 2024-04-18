package pokemon.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import pokemon.Pokemon

@Composable
fun PokemonCardFront(modifier: Modifier, pokemon: Pokemon, bitmap: ImageBitmap?) {
    PokemonCardCommon(modifier = modifier, pokemon = pokemon) {
        bitmap?.run {
            Image(bitmap, pokemon.name, modifier = Modifier.fillMaxSize())
        } ?: Icon(Icons.Default.BrokenImage, pokemon.name, modifier = Modifier.fillMaxSize(.5f))
    }
}