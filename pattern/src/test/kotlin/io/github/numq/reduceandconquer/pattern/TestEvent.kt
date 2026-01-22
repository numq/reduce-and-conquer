package io.github.numq.reduceandconquer.pattern

sealed interface TestEvent {
    data object Incremented : TestEvent

    data object Decremented : TestEvent

    data class ErrorOccurred(val message: String?) : TestEvent
}