package feature

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class Feature<in Command, State, Event>(
    initialState: State,
    val coroutineScope: CoroutineScope,
    private val reducer: Reducer<Command, State, Event>
) {
    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _events = Channel<Event>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val commands = Channel<Command>(Channel.RENDEZVOUS)

    private var onClose: (() -> Unit)? = null

    private suspend fun processCommand(command: Command) {
        val (newState, newEvents) = reducer.reduce(_state.value, command)

        _state.value = newState

        newEvents.forEach { event -> _events.send(event) }
    }

    init {
        coroutineScope.launch {
            commands.consumeEach { command ->
                processCommand(command)
            }
        }
    }

    fun tryExecute(command: Command): Boolean = commands.trySend(command).isSuccess

    suspend fun execute(command: Command) {
        commands.send(command)
    }

    fun invokeOnClose(block: () -> Unit) {
        onClose = block
    }

    fun close() {
        try {
            onClose?.invoke()
        } finally {
            coroutineScope.cancel()

            commands.close()

            _events.close()
        }
    }
}