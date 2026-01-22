package io.github.numq.reduceandconquer.example.navigation

import io.github.numq.reduceandconquer.example.feature.BaseFeature
import kotlinx.coroutines.CoroutineScope

internal class NavigationFeature(
    initialState: NavigationState, scope: CoroutineScope, reducer: NavigationReducer
) : BaseFeature<NavigationState, NavigationCommand, Nothing>(
    initialState = initialState, scope = scope, reducer = reducer
)