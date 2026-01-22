package io.github.numq.reduceandconquer.example.daily

internal sealed interface DailyEvent {
    data class Error(val message: String) : DailyEvent
}