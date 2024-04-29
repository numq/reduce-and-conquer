package feature

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

abstract class Feature<State, Message, Effect : feature.Effect<*>>(
    initialState: State,
    coroutineScope: CoroutineScope,
    messagesBufferCapacity: Int = Int.MAX_VALUE,
    effectsBufferCapacity: Int = Int.MAX_VALUE,
) {
    private val messages = MutableSharedFlow<Message>(extraBufferCapacity = messagesBufferCapacity)

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<Effect>(extraBufferCapacity = effectsBufferCapacity)
    val effects = _effects.asSharedFlow()

    fun dispatchMessage(message: Message) = messages.tryEmit(message)

    fun performEffect(effect: Effect) = _effects.tryEmit(effect)

    abstract suspend fun reduce(state: State, message: Message): State

    init {
        messages.onEach { message ->
            _state.value = reduce(_state.value, message)
        }.launchIn(coroutineScope)
    }
}