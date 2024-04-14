package navigation

sealed interface NavigationMessage {
    data object NavigateToDaily : NavigationMessage
    data object NavigateToPokedex : NavigationMessage
}