package daily

import pokemon.Pokemon
import pokemon.card.PokemonCardSide

data class DailyState(
    val maxAttributeValue: Int? = null,
    val pokemon: Pokemon? = null,
    val cardSide: PokemonCardSide = PokemonCardSide.Front,
)