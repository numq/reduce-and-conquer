package io.github.numq.reduceandconquer.example.navigation

import io.github.numq.reduceandconquer.library.Reducer

internal class NavigationReducer : Reducer<NavigationState, NavigationCommand, Nothing> {
    override fun reduce(state: NavigationState, command: NavigationCommand) = when (command) {
        is NavigationCommand.NavigateToDaily -> transition(NavigationState.Daily)

        is NavigationCommand.NavigateToPokedex -> transition(NavigationState.Pokedex)
    }
}