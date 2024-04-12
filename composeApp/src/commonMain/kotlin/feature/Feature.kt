package feature

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

abstract class Feature<State, Message, Effect : feature.Effect<*>>(
    initialState: State,
    coroutineScope: CoroutineScope,
) {
    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _message = Channel<Message>(Channel.UNLIMITED)
    val message = _message.receiveAsFlow()

    private val _effect = Channel<Effect>(Channel.UNLIMITED)
    val effect = _effect.receiveAsFlow().shareIn(coroutineScope, started = SharingStarted.Lazily)

    fun dispatchMessage(message: Message) = _message.trySend(message).isSuccess

    fun performEffect(effect: Effect) = _effect.trySend(effect).isSuccess

    open suspend fun reduce(state: State, message: Message) = state

    init {
        message.onEach { message ->
            _state.emit(reduce(state.value, message))
        }.launchIn(coroutineScope)
    }
}