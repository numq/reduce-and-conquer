package io.github.numq.reduceandconquer.example.pokedex

import io.github.numq.reduceandconquer.example.pokedex.filter.PokedexFilter
import io.github.numq.reduceandconquer.example.pokedex.sort.PokedexSort
import io.github.numq.reduceandconquer.example.pokemon.Pokemon

data class Pokedex(
    val pokemons: List<Pokemon> = emptyList(),
    val dailyPokemon: Pokemon? = null,
    val attributeRanges: Map<Pokemon.Attribute.Kind, IntRange> = emptyMap(),
    val filters: Map<PokedexFilter.Criteria, PokedexFilter> = emptyMap(),
    val sort: PokedexSort = defaultSort,
    val maxAttributeValue: Int = 0
) {
    companion object {
        val defaultSort = PokedexSort(criteria = PokedexSort.Criteria.NAME, isAscending = true)
    }
}