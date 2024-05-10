package daily

import pokemon.card.PokemonCardSide

sealed interface DailyCommand {
    data object GetMaxAttributeValue : DailyCommand
    data object GetDailyPokemon : DailyCommand
    data class FlipCard(val cardSide: PokemonCardSide) : DailyCommand
}