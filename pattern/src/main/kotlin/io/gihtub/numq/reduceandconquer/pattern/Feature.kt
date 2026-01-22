package io.gihtub.numq.reduceandconquer.pattern

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Feature<out State, in Command, out Event> : AutoCloseable {
    val state: StateFlow<State>

    val events: Flow<Event>

    suspend fun execute(command: Command)
}