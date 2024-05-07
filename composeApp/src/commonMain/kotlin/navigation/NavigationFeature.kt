package navigation

import feature.Feature

class NavigationFeature(reducer: NavigationReducer) : Feature<NavigationCommand, NavigationState, NavigationEvent>(
    initialState = NavigationState.Daily,
    reducer = reducer
)