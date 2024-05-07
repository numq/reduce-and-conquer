package feature

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class Feature<in Command, out State, Event>(
    initialState: State,
    private val reducer: Reducer<Command, State, Event>,
) {
    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

    suspend fun trigger(event: Event) = runCatching {
        _events.emit(event)
    }.isSuccess

    suspend fun execute(command: Command) = runCatching {
        reducer.reduce(state.value, command).let { (state, event) ->
            _state.emit(state)
            event?.run { _events.emit(this) }
        }
    }.isSuccess
}