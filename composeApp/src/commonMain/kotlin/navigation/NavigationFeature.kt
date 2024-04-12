package navigation

import feature.Feature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class NavigationFeature(
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
) : Feature<NavigationState, NavigationMessage, NavigationEffect>(
    initialState = NavigationState(),
    coroutineScope = coroutineScope
) {
    override suspend fun reduce(state: NavigationState, message: NavigationMessage) = when (message) {
        is NavigationMessage.NavigateTo -> state.copy(destination = message.destination)
    }
}