package io.github.numq.reduceandconquer.example.navigation

sealed interface NavigationCommand {
    data object NavigateToDaily : NavigationCommand

    data object NavigateToPokedex : NavigationCommand
}