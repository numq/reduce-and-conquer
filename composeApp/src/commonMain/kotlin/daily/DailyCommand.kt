package daily

sealed interface DailyCommand {
    data object GetMaxAttributeValue : DailyCommand
    data object GetDailyPokemon : DailyCommand
}