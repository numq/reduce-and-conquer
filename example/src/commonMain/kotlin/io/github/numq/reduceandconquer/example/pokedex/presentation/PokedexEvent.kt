package io.github.numq.reduceandconquer.example.pokedex.presentation

sealed interface PokedexEvent {
    data class Error(val message: String) : PokedexEvent

    data object ScrollToStart : PokedexEvent
}