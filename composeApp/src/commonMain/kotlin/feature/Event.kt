package feature

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.coroutines.cancellation.CancellationException

internal interface Event {
    val payload: Any?

    val timestamp: Instant

    abstract class Collectable<out T>(override val payload: Any? = null) : Event {
        abstract val key: Any

        abstract val flow: Flow<T>

        override val timestamp = Clock.System.now()
    }

    data class Timeout(val exception: TimeoutCancellationException, override val payload: Any? = null) : Event {
        override val timestamp = Clock.System.now()
    }

    data class Cancellation(val exception: CancellationException, override val payload: Any? = null) : Event {
        override val timestamp = Clock.System.now()
    }

    data class Failure(val throwable: Throwable, override val payload: Any? = null) : Event {
        override val timestamp = Clock.System.now()
    }
}