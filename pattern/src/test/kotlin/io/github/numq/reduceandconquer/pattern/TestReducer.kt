package io.github.numq.reduceandconquer.pattern

import io.gihtub.numq.reduceandconquer.pattern.*
import kotlinx.coroutines.delay

internal class TestReducer : Reducer<TestState, TestCommand, TestEvent> {
    override fun reduce(state: TestState, command: TestCommand) = when (command) {
        is TestCommand.Increment -> transition(state.copy(count = state.count + 1)).event(TestEvent.Incremented)

        is TestCommand.Decrement -> transition(state.copy(count = state.count - 1)).event(TestEvent.Decremented)

        is TestCommand.AsyncIncrement -> transition(state).effect(action(key = "async_op") {
            delay(100)

            TestCommand.AsyncSuccess(state.count + 1)
        })

        is TestCommand.AsyncSuccess -> transition(state.copy(count = command.value)).event(TestEvent.Incremented)

        is TestCommand.AsyncFailure -> transition(state).effect(action(key = "fail_op") {
            try {
                throw RuntimeException("Test Error")
            } catch (throwable: Throwable) {
                TestCommand.HandleError(throwable)
            }
        })

        is TestCommand.HandleError -> transition(state).event(TestEvent.ErrorOccurred(command.error.message))

        is TestCommand.StartStream -> transition(state).effect(stream(key = "test_stream", flow = command.flow))
    }
}