package feature

sealed interface TestCommand {
    data object Increment : TestCommand

    data object Decrement : TestCommand

    data object IncrementByTwo : TestCommand

    data object DecrementByTwo : TestCommand
}
