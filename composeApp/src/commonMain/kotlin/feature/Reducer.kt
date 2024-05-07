package feature

interface Reducer<in Command, State, out Event> {
    suspend fun reduce(state: State, command: Command): Transition<State, Event>
}

fun <State, Event> Reducer<*, State, Event>.transition(state: State) =
    Transition<State, Event>(state = state, event = null)

fun <State, Event> Reducer<*, State, Event>.transition(state: State, event: Event) =
    Transition<State, Event>(state = state, event = event)