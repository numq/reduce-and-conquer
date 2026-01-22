package io.github.numq.reduceandconquer.example.daily

import io.github.numq.reduceandconquer.example.card.FlippableCard
import io.github.numq.reduceandconquer.example.feature.*
import io.github.numq.reduceandconquer.example.pokedex.GetPokedex
import kotlinx.coroutines.flow.map

internal class DailyReducer(private val getPokedex: GetPokedex) : Reducer<DailyState, DailyCommand, DailyEvent> {
    override fun reduce(state: DailyState, command: DailyCommand) = when (command) {
        is DailyCommand.HandleThrowable -> transition(state).event(
            DailyEvent.Error(
                message = command.throwable.message ?: "Unknown error"
            )
        )

        is DailyCommand.Initialize -> transition(state).effects(
            action(key = command.key, block = {
                getPokedex.execute(Unit).fold(
                    onSuccess = DailyCommand::InitializeSuccess, onFailure = DailyCommand::HandleThrowable
                )
            })
        )

        is DailyCommand.InitializeSuccess -> transition(state).effect(
            stream(key = command.key, flow = command.flow.map(DailyCommand::HandlePokedex))
        )

        is DailyCommand.HandlePokedex -> {
            val pokedex = command.pokedex

            transition(
                state.copy(
                    maxAttributeValue = pokedex.maxAttributeValue, card = pokedex.dailyPokemon?.let(::FlippableCard)
                )
            )
        }

        is DailyCommand.FlipCard -> transition(state.copy(card = state.card?.flip()))
    }
}