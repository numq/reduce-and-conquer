package io.github.numq.reduceandconquer.library

sealed interface TestEvent {
    data object Incremented : TestEvent

    data object Decremented : TestEvent

    data class ErrorOccurred(val message: String?) : TestEvent
}