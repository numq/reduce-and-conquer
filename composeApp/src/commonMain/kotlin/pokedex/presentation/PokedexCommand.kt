package pokedex.presentation

import pokedex.filter.PokedexFilter
import pokedex.sort.PokedexSort

sealed interface PokedexCommand {
    sealed interface Pokemons : PokedexCommand {
        data object GetMaxAttributeValue : Pokemons
        data class GetPokemons(val skip: Long, val limit: Long) : Pokemons
        data object LoadMorePokemons : Pokemons
        data object ResetScroll : Pokemons
    }

    sealed interface Filter : PokedexCommand {
        data object InitializeFilters : Filter
        data object ToggleFilterMode : Filter
        data class SelectFilter(val criteria: PokedexFilter.Criteria) : Filter
        data class UpdateFilter(val filter: PokedexFilter) : Filter
        data class ResetFilter(val criteria: PokedexFilter.Criteria) : Filter
        data object CloseFilter : Filter
        data object ResetFilters : Filter
    }

    sealed interface Sort : PokedexCommand {
        data object ToggleSortMode : Sort
        data class SortPokemons(val sort: PokedexSort) : Sort
    }
}