package navigation

import feature.Reducer

internal class NavigationReducer : Reducer<NavigationCommand, NavigationState> {
    override suspend fun reduce(state: NavigationState, command: NavigationCommand) = when (command) {
        is NavigationCommand.NavigateToDaily -> transition(NavigationState.Daily)

        is NavigationCommand.NavigateToPokedex -> transition(NavigationState.Pokedex)
    }
}