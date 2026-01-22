package io.github.numq.reduceandconquer.example.daily

import io.github.numq.reduceandconquer.example.card.FlippableCard
import io.github.numq.reduceandconquer.example.pokemon.Pokemon

data class DailyState(
    val maxAttributeValue: Int? = null,
    val card: FlippableCard<Pokemon>? = null,
)