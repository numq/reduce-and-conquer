package pokedex.presentation

import pokedex.filter.PokedexFilter
import pokedex.sort.PokedexSort

sealed interface PokedexMessage {
    sealed interface Pokemons : PokedexMessage {
        data object GetMaxAttributeValue : Pokemons
        data class GetPokemons(val skip: Long, val limit: Long) : Pokemons
        data object LoadMorePokemons : Pokemons
    }

    sealed interface Filter : PokedexMessage {
        data object InitializeFilters : Filter
        data object ToggleFilterMode : Filter
        data class SelectFilter(val criteria: PokedexFilter.Criteria) : Filter
        data class UpdateFilter(val filter: PokedexFilter) : Filter
        data object CloseFilter : Filter
        data object ResetFilters : Filter
    }

    sealed interface Sort : PokedexMessage {
        data object ToggleSortMode : Sort
        data class SortPokemons(val sort: PokedexSort) : Sort
    }
}