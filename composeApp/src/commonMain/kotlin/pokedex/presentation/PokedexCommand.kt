package pokedex.presentation

import card.FlippableCard
import pokedex.filter.PokedexFilter
import pokedex.sort.PokedexSort
import pokemon.Pokemon

sealed interface PokedexCommand {
    sealed interface Cards : PokedexCommand {
        data object GetMaxAttributeValue : Cards
        data class GetCards(val skip: Long, val limit: Long) : Cards
        data object LoadMoreCards : Cards
        data class FlipCard(val card: FlippableCard<Pokemon>) : Cards
        data object ResetScroll : Cards
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