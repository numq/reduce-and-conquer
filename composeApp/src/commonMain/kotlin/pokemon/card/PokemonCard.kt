package pokemon.card

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import image.ImageLoader
import pokemon.Pokemon

@Composable
fun PokemonCard(
    modifier: Modifier,
    pokemon: Pokemon,
    maxAttributeValue: Int,
    cardSide: PokemonCardSide,
    setCardSide: (PokemonCardSide) -> Unit,
) {
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

    val bitmap = remember(pokemon) { pokemon.imageBytes?.let { ImageLoader.loadBitmap(it) } }

    val animatedRotation = animateFloatAsState(
        targetValue = cardSide.angle, animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing,
        )
    )

    Box(modifier = Modifier.graphicsLayer {
        rotationY = animatedRotation.value
        cameraDistance = 16f * density
    }.clickable(
        interactionSource = MutableInteractionSource(), indication = null
    ) {
        setCardSide(cardSide.flip())
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