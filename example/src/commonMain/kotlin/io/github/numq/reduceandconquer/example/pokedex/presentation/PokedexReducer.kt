package io.github.numq.reduceandconquer.example.pokedex.presentation

import io.github.numq.reduceandconquer.example.card.FlippableCard
import io.github.numq.reduceandconquer.example.feature.*
import io.github.numq.reduceandconquer.example.pokedex.GetPokedex
import io.github.numq.reduceandconquer.example.pokedex.filter.PokedexFilter
import io.github.numq.reduceandconquer.example.pokedex.presentation.filter.FilterReducer
import io.github.numq.reduceandconquer.example.pokedex.presentation.sort.SortReducer
import kotlinx.coroutines.flow.map

internal class PokedexReducer(
    private val getPokedex: GetPokedex,
    private val filterReducer: FilterReducer,
    private val sortReducer: SortReducer,
) : Reducer<PokedexState, PokedexCommand, PokedexEvent> {
    override fun reduce(state: PokedexState, command: PokedexCommand) = when (command) {
        is PokedexCommand.HandleFailure -> transition(state).event(
            PokedexEvent.Error(message = command.throwable.message ?: "Unknown error")
        )

        is PokedexCommand.Initialize -> transition(state).effect(action(key = command.key, block = {
            getPokedex.execute(Unit).fold(
                onSuccess = PokedexCommand::InitializeSuccess, onFailure = PokedexCommand::HandleFailure
            )
        }))

        is PokedexCommand.InitializeSuccess -> transition(state).effect(
            stream(key = command.key, flow = command.flow.map(PokedexCommand::HandlePokedex))
        )

        is PokedexCommand.HandlePokedex -> with(command.pokedex) {
            transition(
                state.copy(
                    maxAttributeValue = maxAttributeValue,
                    cards = pokemons.map(::FlippableCard),
                    filters = filters.values.filterNot { filter ->
                        filter is PokedexFilter.Name
                    },
                    selectedFilter = state.selectedFilter?.criteria?.let(filters::get),
                    sort = sort
                )
            )
        }

        is PokedexCommand.FlipCard -> transition(
            state.copy(
                cards = state.cards.map { card ->
                    when (card.item.id) {
                        command.card.item.id -> card.flip()

                        else -> card
                    }
                })
        )

        is PokedexCommand.ScrollToStart -> transition(state).event(PokedexEvent.ScrollToStart)

        is PokedexCommand.Filter -> filterReducer.reduce(state, command)

        is PokedexCommand.Sort -> sortReducer.reduce(state, command)
    }
}