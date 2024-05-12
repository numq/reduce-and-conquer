package navigation

import feature.Reducer

class NavigationReducer : Reducer<NavigationCommand, NavigationState, NavigationEvent> {
    override suspend fun reduce(state: NavigationState, command: NavigationCommand) = when (command) {
        is NavigationCommand.NavigateToDaily -> transition(NavigationState.Daily)

        is NavigationCommand.NavigateToPokedex -> transition(NavigationState.Pokedex)
    }
}