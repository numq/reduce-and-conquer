package feature

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

abstract class Feature<State, Message, Effect : feature.Effect<*>>(
    initialState: State,
    coroutineScope: CoroutineScope,
) {
    private val messages = Channel<Message>(Channel.UNLIMITED)

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _effects = Channel<Effect>(Channel.UNLIMITED)
    val effects = _effects.receiveAsFlow().shareIn(scope = coroutineScope, started = SharingStarted.Lazily)

    fun dispatchMessage(message: Message) = messages.trySend(message).isSuccess

    fun performEffect(effect: Effect) = _effects.trySend(effect).isSuccess

    open suspend fun reduce(state: State, message: Message) = state

    init {
        messages.receiveAsFlow().onEach { message ->
            _state.value = reduce(_state.value, message)
        }.launchIn(coroutineScope)
    }
}