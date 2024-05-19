package feature

interface Reducer<in Command, State, Event> {
    suspend fun reduce(state: State, command: Command): Transition<State, Event>

    fun transition(state: State, events: List<Event> = emptyList()) = Transition(state = state, events = events)

    fun transition(state: State, event: Event) = Transition(state = state, events = listOf(event))
}
