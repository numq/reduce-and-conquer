package io.github.numq.reduceandconquer.example.pokedex.presentation

import io.github.numq.reduceandconquer.example.card.FlippableCard
import io.github.numq.reduceandconquer.example.pokedex.Pokedex
import io.github.numq.reduceandconquer.example.pokedex.filter.PokedexFilter
import io.github.numq.reduceandconquer.example.pokedex.sort.PokedexSort
import io.github.numq.reduceandconquer.example.pokemon.Pokemon
import kotlinx.coroutines.flow.Flow

sealed interface PokedexCommand {
    enum class Key { INITIALIZE, INITIALIZE_SUCCESS }

    data class HandleFailure(val throwable: Throwable) : PokedexCommand

    data object Initialize : PokedexCommand {
        val key = Key.INITIALIZE
    }

    data class InitializeSuccess(val flow: Flow<Pokedex>) : PokedexCommand {
        val key = Key.INITIALIZE_SUCCESS
    }

    data class HandlePokedex(val pokedex: Pokedex) : PokedexCommand

    data class FlipCard(val card: FlippableCard<Pokemon>) : PokedexCommand

    data object ScrollToStart : PokedexCommand

    sealed interface Filter : PokedexCommand {
        enum class Key { UPDATE_FILTER, RESET_FILTER, RESET_FILTERS }

        data class HandleFailure(val throwable: Throwable) : Filter

        data object ToggleFilterMode : Filter

        data class UpdateFilter(val filter: PokedexFilter) : Filter {
            val key = Key.UPDATE_FILTER
        }

        data class UpdateFilterSuccess(val filter: PokedexFilter) : Filter

        data class SelectFilter(val criteria: PokedexFilter.Criteria) : Filter

        data class ResetFilter(val criteria: PokedexFilter.Criteria) : Filter {
            val key = Key.RESET_FILTER
        }

        data object ResetFilterSuccess : Filter

        data object ResetFilters : Filter {
            val key = Key.RESET_FILTERS
        }

        data object ResetFiltersSuccess : Filter

        data object CloseFilter : Filter
    }

    sealed interface Sort : PokedexCommand {
        enum class Key { SORT_POKEMONS }

        data class HandleFailure(val throwable: Throwable) : Sort

        data object ToggleSortMode : Sort

        data class SortPokemons(val sort: PokedexSort) : Sort {
            val key = Key.SORT_POKEMONS
        }

        data object SortPokemonsSuccess : Sort
    }
}