package pokedex.presentation

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import feature.Effect

sealed interface PokedexEffect : Effect<Uuid> {
    sealed class Error(val message: String, override val key: Uuid = uuid4()) : PokedexEffect {
        data class GetMaxAttributeValue(override val key: Uuid = uuid4(), ) : Error("Unable to get max attribute value")
        data class GetPokemons(override val key: Uuid = uuid4()) : Error("Unable to get pokemons")
        data class LoadMore(override val key: Uuid = uuid4()) : Error("Unable to load more")
        data class UnableToInitializeFilters(override val key: Uuid = uuid4()) : Error("Unable to initialize filters")
        data class UnableToSelectFilter(override val key: Uuid = uuid4()) : Error("Unable to select filter")
        data class UnableToUpdateFilter(override val key: Uuid = uuid4()) : Error("Unable to update filter")
        data class UnableToResetFilters(override val key: Uuid = uuid4()) : Error("Unable to reset filters")
        data class UnableToSelectSort(override val key: Uuid = uuid4()) : Error("Unable to select sort")
    }

    data class ScrollToStart(override val key: Uuid = uuid4()) : PokedexEffect

    data class ResetScroll(override val key: Uuid = uuid4()) : PokedexEffect
}