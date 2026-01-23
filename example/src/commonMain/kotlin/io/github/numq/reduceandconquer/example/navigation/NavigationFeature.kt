package io.github.numq.reduceandconquer.example.navigation

import io.github.numq.reduceandconquer.example.feature.Feature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

internal class NavigationFeature(
    reducer: NavigationReducer
) : Feature<NavigationState, NavigationCommand, Nothing> by Feature(
    initialState = NavigationState.Daily,
    scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
    reducer = reducer
)