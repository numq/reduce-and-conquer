package io.github.numq.reduceandconquer.example.pokedex.presentation

import io.github.numq.reduceandconquer.example.card.FlippableCard
import io.github.numq.reduceandconquer.example.pokedex.filter.PokedexFilter
import io.github.numq.reduceandconquer.example.pokedex.sort.PokedexSort
import io.github.numq.reduceandconquer.example.pokemon.Pokemon

data class PokedexState(
    val maxAttributeValue: Int? = null,
    val cards: List<FlippableCard<Pokemon>> = emptyList(),
    val interactionMode: PokedexInteractionMode = PokedexInteractionMode.NONE,
    val filters: List<PokedexFilter> = emptyList(),
    val selectedFilter: PokedexFilter? = null,
    val sort: PokedexSort = PokedexSort(criteria = PokedexSort.Criteria.NAME, isAscending = true),
)