package navigation

import feature.Feature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class NavigationFeature(
    initialState: NavigationState = NavigationState.Daily,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
) : Feature<NavigationState, NavigationMessage, NavigationEffect>(
    initialState = initialState,
    coroutineScope = coroutineScope
) {
    override suspend fun reduce(state: NavigationState, message: NavigationMessage) = when (message) {
        is NavigationMessage.NavigateToDaily -> NavigationState.Daily

        is NavigationMessage.NavigateToPokedex -> NavigationState.Pokedex
    }
}