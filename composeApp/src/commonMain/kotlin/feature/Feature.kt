package feature

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class Feature<in Command, out State, out Event>(
    initialState: State,
    eventBufferCapacity: Int = 64,
    private val reducer: Reducer<Command, State, Event>,
) {
    val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = eventBufferCapacity)
    val events = _events.asSharedFlow()

    suspend fun execute(command: Command) = runCatching {
        reducer.reduce(state.value, command).let { (state, events) ->
            _state.emit(state)
            events.forEach { event -> _events.emit(event) }
        }
    }.isSuccess
}