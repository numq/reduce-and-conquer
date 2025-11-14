package pokedex.presentation

import feature.Event
import kotlinx.datetime.Clock

internal sealed interface PokedexEvent : Event {
    sealed class Error(val message: String) : PokedexEvent {
        data object GetMaxAttributeValue : Error("Unable to get max attribute value") {
            override val payload = null

            override val timestamp = Clock.System.now()
        }

        data object GetPokemons : Error("Unable to get pokemons") {
            override val payload = null

            override val timestamp = Clock.System.now()
        }

        data object LoadMore : Error("Unable to load more") {
            override val payload = null

            override val timestamp = Clock.System.now()
        }

        data object UnableToInitializeFilters : Error("Unable to initialize filters") {
            override val payload = null

            override val timestamp = Clock.System.now()
        }

        data object UnableToSelectFilter : Error("Unable to select filter") {
            override val payload = null

            override val timestamp = Clock.System.now()
        }

        data object UnableToUpdateFilter : Error("Unable to update filter") {
            override val payload = null

            override val timestamp = Clock.System.now()
        }

        data object UnableToResetFilter : Error("Unable to reset filter") {
            override val payload = null

            override val timestamp = Clock.System.now()
        }

        data object UnableToResetFilters : Error("Unable to reset filters") {
            override val payload = null

            override val timestamp = Clock.System.now()
        }

        data object UnableToSelectSort : Error("Unable to select sort") {
            override val payload = null

            override val timestamp = Clock.System.now()
        }
    }

    data object ScrollToStart : PokedexEvent {
        override val payload = null

        override val timestamp = Clock.System.now()
    }

    data object ResetScroll : PokedexEvent {
        override val payload = null

        override val timestamp = Clock.System.now()
    }
}