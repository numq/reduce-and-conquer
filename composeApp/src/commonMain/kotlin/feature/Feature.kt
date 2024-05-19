package feature

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

abstract class Feature<in Command, out State, out Event>(
    initialState: State,
    private val reducer: Reducer<Command, State, Event>,
) {
    val coroutineScope = CoroutineScope(Dispatchers.Default)

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _events = Channel<Event>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    suspend fun execute(command: Command) = runCatching {
        reducer.reduce(state.value, command).let { (state, events) ->
            _state.emit(state)
            events.forEach { event -> _events.send(event) }
        }
    }.isSuccess
}