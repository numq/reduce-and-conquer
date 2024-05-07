package feature

data class Transition<out State, out Event>(
    val state: State,
    val event: Event?,
)