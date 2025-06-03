package navigation

import feature.Feature
import kotlinx.coroutines.CoroutineScope

class NavigationFeature(
    coroutineScope: CoroutineScope,
    reducer: NavigationReducer
) : Feature<NavigationCommand, NavigationState, NavigationEvent>(
    initialState = NavigationState.Daily,
    coroutineScope = coroutineScope,
    reducer = reducer
)