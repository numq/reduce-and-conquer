package feature

class TestReducer : Reducer<TestCommand, TestState, TestEvent> {
    override suspend fun reduce(state: TestState, command: TestCommand): Transition<TestState, TestEvent> {
        return when (command) {
            is TestCommand.Increment -> transition(state.copy(count = state.count + 1), TestEvent.Incremented)
            is TestCommand.Decrement -> transition(state.copy(count = state.count - 1), TestEvent.Decremented)
        }
    }
}
