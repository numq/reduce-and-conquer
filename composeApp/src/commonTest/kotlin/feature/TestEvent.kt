package feature

interface TestEvent {
    data object Incremented : TestEvent

    data object Decremented : TestEvent
}