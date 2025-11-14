package navigation

import feature.Feature

internal class NavigationFeature(
    private val feature: Feature<NavigationCommand, NavigationState>
) : Feature<NavigationCommand, NavigationState> by feature