package daily

sealed interface DailyMessage {
    data object GetMaxAttributeValue : DailyMessage
    data object GetDailyPokemon : DailyMessage
}