package navigation

sealed interface NavigationState {
    data object Daily : NavigationState
    data object Pokedex : NavigationState
}