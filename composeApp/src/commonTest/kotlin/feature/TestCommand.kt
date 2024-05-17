package feature

sealed interface TestCommand {
    data object Increment : TestCommand

    data object Decrement : TestCommand
}
