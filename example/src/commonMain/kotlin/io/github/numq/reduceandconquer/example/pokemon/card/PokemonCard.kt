package io.github.numq.reduceandconquer.example.pokemon.card

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import io.github.numq.reduceandconquer.example.card.FlippableCard
import io.github.numq.reduceandconquer.example.dispatcher.ioDispatcher
import io.github.numq.reduceandconquer.example.file.FileProvider
import io.github.numq.reduceandconquer.example.image.ImageLoader
import io.github.numq.reduceandconquer.example.pokemon.Pokemon
import kotlinx.coroutines.withContext
import org.koin.compose.koinInject

@Composable
fun PokemonCard(
    modifier: Modifier,
    card: FlippableCard<Pokemon>,
    maxAttributeValue: Int,
    flip: () -> Unit,
) {
    val fileProvider = koinInject<FileProvider>()

    val pokemon = card.item

    val side = card.side

    val attributes = remember(pokemon.attributes) {
        listOf(
            pokemon.attributes.specialAttack,
            pokemon.attributes.specialDefense,
            pokemon.attributes.speed,
            pokemon.attributes.basicDefense,
            pokemon.attributes.basicAttack,
            pokemon.attributes.hp
        )
    }

    val bitmap by produceState<ImageBitmap?>(null, pokemon.id) {
        value = pokemon.imagePath?.let { imagePath ->
            withContext(ioDispatcher) {
                fileProvider.open(path = imagePath).getOrNull()
            }
        }?.let(ImageLoader::loadBitmap)
    }

    val animatedRotation = animateFloatAsState(
        targetValue = side.angle, animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing,
        )
    )

    Box(
        modifier = Modifier.graphicsLayer {
            rotationY = animatedRotation.value
            cameraDistance = 16f * density
        }.clickable(
            interactionSource = MutableInteractionSource(), indication = null
        ) {
            flip()
        }) {
        if (animatedRotation.value <= 90f) Card {
            PokemonCardFront(
                modifier = Modifier.then(modifier), pokemon = pokemon, bitmap = bitmap
            )
        } else Card(modifier = Modifier.graphicsLayer(rotationY = 180f)) {
            PokemonCardBack(
                modifier = modifier, pokemon = pokemon, attributes = attributes, maxAttributeValue = maxAttributeValue
            )
        }
    }
}