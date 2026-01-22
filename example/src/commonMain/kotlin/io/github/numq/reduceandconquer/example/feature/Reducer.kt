package io.github.numq.reduceandconquer.example.feature

import kotlinx.coroutines.flow.Flow

internal interface Reducer<State, in Command, out Event> {
    fun reduce(state: State, command: Command): Transition<State, Event>

    fun transition(state: State) = Transition<State, Event>(state = state, events = emptyList(), effects = emptyList())
}

internal fun <State, Command, Event> Reducer<State, Command, Event>.stream(
    key: Any,
    flow: Flow<Command>,
    strategy: Effect.Stream.Strategy = Effect.Stream.Strategy.Sequential,
    fallback: (suspend (Throwable) -> Command)? = null
) = Effect.Stream(key, flow, strategy, fallback)

internal fun <State, Command, Event> Reducer<State, Command, Event>.action(
    key: Any, fallback: (suspend (Throwable) -> Command)? = null, block: suspend () -> Command
) = Effect.Action(key, fallback, block)

internal fun <State, Command, Event> Reducer<State, Command, Event>.cancel(key: Any) = Effect.Cancel(key)