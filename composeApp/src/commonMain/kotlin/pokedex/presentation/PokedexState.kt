package pokedex.presentation

import pokedex.filter.PokedexFilter
import pokedex.sort.PokedexSort
import pokemon.Pokemon

data class PokedexState(
    val maxAttributeValue: Int? = null,
    val pokemons: List<Pokemon> = emptyList(),
    val interactionMode: PokedexInteractionMode = PokedexInteractionMode.NONE,
    val filters: List<PokedexFilter> = emptyList(),
    val isFiltered: Boolean = false,
    val selectedFilter: PokedexFilter? = null,
    val sort: PokedexSort = PokedexSort(criteria = PokedexSort.Criteria.NAME, isAscending = true),
)