package io.github.numq.reduceandconquer.example.pokedex.presentation.filter

import io.github.numq.reduceandconquer.example.feature.Reducer
import io.github.numq.reduceandconquer.example.feature.action
import io.github.numq.reduceandconquer.example.feature.effect
import io.github.numq.reduceandconquer.example.feature.event
import io.github.numq.reduceandconquer.example.pokedex.filter.PokedexFilter
import io.github.numq.reduceandconquer.example.pokedex.filter.ResetFilter
import io.github.numq.reduceandconquer.example.pokedex.filter.ResetFilters
import io.github.numq.reduceandconquer.example.pokedex.filter.UpdateFilter
import io.github.numq.reduceandconquer.example.pokedex.presentation.PokedexCommand
import io.github.numq.reduceandconquer.example.pokedex.presentation.PokedexEvent
import io.github.numq.reduceandconquer.example.pokedex.presentation.PokedexInteractionMode
import io.github.numq.reduceandconquer.example.pokedex.presentation.PokedexState

internal class FilterReducer(
    private val resetFilter: ResetFilter,
    private val resetFilters: ResetFilters,
    private val updateFilter: UpdateFilter,
) : Reducer<PokedexState, PokedexCommand.Filter, PokedexEvent> {
    override fun reduce(state: PokedexState, command: PokedexCommand.Filter) = when (command) {
        is PokedexCommand.Filter.HandleFailure -> transition(state).event(
            PokedexEvent.Error(message = command.throwable.message ?: "Unknown error")
        )

        is PokedexCommand.Filter.ToggleFilterMode -> transition(state.copy(interactionMode = PokedexInteractionMode.FILTER.takeIf { mode ->
            state.interactionMode != mode
        } ?: PokedexInteractionMode.NONE))

        is PokedexCommand.Filter.UpdateFilter -> transition(state).effect(action(key = command.key, block = {
            val filter = command.filter

            updateFilter.execute(UpdateFilter.Input(filter = filter)).fold(onSuccess = {
                PokedexCommand.Filter.UpdateFilterSuccess(filter = filter)
            }, onFailure = PokedexCommand.Filter::HandleFailure)
        }))

        is PokedexCommand.Filter.UpdateFilterSuccess -> transition(state.copy(selectedFilter = command.filter.takeUnless { filter ->
            filter is PokedexFilter.Name
        }))

        is PokedexCommand.Filter.SelectFilter -> transition(state.copy(selectedFilter = state.filters.find { filter ->
            filter.criteria == command.criteria
        }))

        is PokedexCommand.Filter.ResetFilter -> transition(state).effect(action(key = command.key, block = {
            resetFilter.execute(ResetFilter.Input(criteria = command.criteria)).fold(onSuccess = {
                PokedexCommand.Filter.ResetFilterSuccess
            }, onFailure = PokedexCommand.Filter::HandleFailure)
        }))

        is PokedexCommand.Filter.ResetFilterSuccess -> transition(state)

        is PokedexCommand.Filter.ResetFilters -> transition(state).effect(action(key = command.key, block = {
            resetFilters.execute(Unit).fold(onSuccess = {
                PokedexCommand.Filter.ResetFiltersSuccess
            }, onFailure = PokedexCommand.Filter::HandleFailure)
        }))

        is PokedexCommand.Filter.ResetFiltersSuccess -> transition(state)

        is PokedexCommand.Filter.CloseFilter -> transition(state.copy(selectedFilter = null))
    }
}