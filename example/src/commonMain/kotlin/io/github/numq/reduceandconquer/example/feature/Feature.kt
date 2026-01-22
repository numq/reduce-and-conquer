package io.github.numq.reduceandconquer.example.feature

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalStdlibApi::class)
internal interface Feature<out State, in Command, out Event> : AutoCloseable {
    val state: StateFlow<State>

    val events: Flow<Event>

    suspend fun execute(command: Command)
}