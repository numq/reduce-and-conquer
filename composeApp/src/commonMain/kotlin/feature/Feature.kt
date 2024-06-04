package feature

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

abstract class Feature<in Command, out State, Event>(
    initialState: State,
    eventBufferCapacity: Int = 64,
    private val reducer: Reducer<Command, State, Event>,
) {
    private val mutex = Mutex()

    private val jobsOnce = mutableMapOf<Any, Job>()

    private val jobsEach = mutableMapOf<Any, MutableList<Job>>()

    private val _state = MutableStateFlow(initialState)

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = eventBufferCapacity)

    val state = _state.asStateFlow()

    val events = _events.asSharedFlow()

    val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    suspend fun <T> Flow<T>.collectOnce(key: Any): Boolean = runCatching {
        mutex.withLock {
            jobsOnce[key]?.cancel()
            jobsOnce[key] = onCompletion { mutex.withLock { jobsOnce.remove(key) } }.launchIn(coroutineScope)
        }
    }.isSuccess

    suspend fun <T> Flow<T>.collectLatestOnce(key: Any, action: suspend (T) -> Unit): Boolean = runCatching {
        mutex.withLock {
            jobsOnce[key]?.cancel()
            jobsOnce[key] = coroutineScope.launch {
                onCompletion { mutex.withLock { jobsOnce.remove(key) } }.collectLatest(action)
            }
        }
    }.isSuccess

    suspend fun <T> Flow<T>.collectEach(key: Any): Boolean = runCatching {
        mutex.withLock {
            val jobs = jobsEach[key] ?: mutableListOf()
            jobsEach[key] = jobs.apply {
                val job = launchIn(coroutineScope)
                job.invokeOnCompletion {
                    coroutineScope.launch {
                        mutex.withLock {
                            jobs.remove(job)
                            if (jobs.isEmpty()) jobsEach.remove(key)
                        }
                    }
                }
                add(job)
            }
        }
    }.isSuccess

    suspend fun <T> Flow<T>.collectLatestEach(key: Any, action: suspend (T) -> Unit): Boolean = runCatching {
        mutex.withLock {
            val jobs = jobsEach[key] ?: mutableListOf()
            jobsEach[key] = jobs.apply {
                val job = coroutineScope.launch {
                    collectLatest(action)
                }
                job.invokeOnCompletion {
                    coroutineScope.launch {
                        mutex.withLock {
                            jobs.remove(job)
                            if (jobs.isEmpty()) jobsEach.remove(key)
                        }
                    }
                }
                add(job)
            }
        }
    }.isSuccess

    suspend fun stopCollecting(key: Any): Boolean = runCatching {
        mutex.withLock {
            jobsOnce[key]?.cancel()
            jobsOnce.remove(key)
            jobsEach[key]?.forEach(Job::cancel)
            jobsEach.remove(key)
        }
    }.isSuccess

    suspend fun execute(command: Command) = runCatching {
        reducer.reduce(_state.value, command).let { (state, events) ->
            _state.emit(state)
            events.forEach { event -> _events.emit(event) }
        }
    }.isSuccess

    open fun <Command, State, Event> Feature<Command, State, Event>.invokeOnClose(block: suspend () -> Unit = {}) {
        coroutineScope.launch {
            block()
        }
    }

    fun close() {
        invokeOnClose()
        coroutineScope.cancel()
    }
}