package io.github.numq.reduceandconquer.example.feature

internal data class Transition<out State, out Event>(
    val state: State, val events: List<Event>, val effects: List<Effect>
)

internal fun <State, Event> Transition<State, Event>.event(event: Event) = copy(events = events + event)

internal fun <State, Event> Transition<State, Event>.events(vararg events: Event) = copy(events = this.events + events)

internal fun <State, Event> Transition<State, Event>.effect(effect: Effect) = copy(effects = effects + effect)

internal fun <State, Event> Transition<State, Event>.effects(vararg effects: Effect) =
    copy(effects = this.effects + effects)