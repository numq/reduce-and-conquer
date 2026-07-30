package io.github.numq.reduceandconquer.example.daily

import io.github.numq.reduceandconquer.example.pokedex.Pokedex
import kotlinx.coroutines.flow.Flow

sealed interface DailyCommand {
    enum class Key { INITIALIZE, INITIALIZE_SUCCESS }

    data class HandleThrowable(val throwable: Throwable) : DailyCommand

    data object Initialize : DailyCommand {
        val key = Key.INITIALIZE
    }

    data class InitializeSuccess(val flow: Flow<Pokedex>) : DailyCommand {
        val key = Key.INITIALIZE_SUCCESS
    }

    data class HandlePokedex(val pokedex: Pokedex) : DailyCommand

    data object FlipCard : DailyCommand
}