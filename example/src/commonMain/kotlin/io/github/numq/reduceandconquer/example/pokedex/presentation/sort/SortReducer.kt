package io.github.numq.reduceandconquer.example.pokedex.presentation.sort

import io.github.numq.reduceandconquer.example.feature.Effect
import io.github.numq.reduceandconquer.example.feature.Reducer
import io.github.numq.reduceandconquer.example.feature.effect
import io.github.numq.reduceandconquer.example.feature.event
import io.github.numq.reduceandconquer.example.pokedex.presentation.PokedexCommand
import io.github.numq.reduceandconquer.example.pokedex.presentation.PokedexEvent
import io.github.numq.reduceandconquer.example.pokedex.presentation.PokedexInteractionMode
import io.github.numq.reduceandconquer.example.pokedex.presentation.PokedexState
import io.github.numq.reduceandconquer.example.pokedex.sort.ChangeSort

internal class SortReducer(
    private val changeSort: ChangeSort,
) : Reducer<PokedexState, PokedexCommand.Sort, PokedexEvent> {
    override fun reduce(state: PokedexState, command: PokedexCommand.Sort) = when (command) {
        is PokedexCommand.Sort.HandleFailure -> transition(state).event(
            PokedexEvent.Error(message = command.throwable.message ?: "Unknown error")
        )

        is PokedexCommand.Sort.ToggleSortMode -> transition(state.copy(interactionMode = PokedexInteractionMode.SORT.takeIf { mode ->
            state.interactionMode != mode
        } ?: PokedexInteractionMode.NONE))

        is PokedexCommand.Sort.SortPokemons -> transition(state).effect(
            Effect.Action(key = command.key, block = {
                changeSort.execute(command.sort).fold(onSuccess = {
                    PokedexCommand.Sort.SortPokemonsSuccess
                }, onFailure = PokedexCommand.Sort::HandleFailure)
            })
        )

        is PokedexCommand.Sort.SortPokemonsSuccess -> transition(state)
    }
}