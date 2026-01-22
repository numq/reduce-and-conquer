package io.github.numq.reduceandconquer.pattern

import kotlinx.coroutines.flow.Flow

sealed interface TestCommand {
    data object Increment : TestCommand

    data object Decrement : TestCommand

    data object AsyncIncrement : TestCommand

    data class AsyncSuccess(val value: Int) : TestCommand

    data object AsyncFailure : TestCommand

    data class StartStream(val flow: Flow<TestCommand>) : TestCommand

    data class HandleError(val error: Throwable) : TestCommand
}