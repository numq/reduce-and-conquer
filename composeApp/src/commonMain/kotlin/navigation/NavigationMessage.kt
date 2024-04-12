package navigation

sealed interface NavigationMessage {
    data class NavigateTo(val destination: Destination) : NavigationMessage
}