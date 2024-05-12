package feature

interface Reducer<in Command, State, Event> {
    suspend fun reduce(state: State, command: Command): Transition<State, Event>

    fun transition(state: State) = Transition<State, Event>(state = state, event = null)

    fun transition(state: State, event: Event) = Transition<State, Event>(state = state, event = event)
}
