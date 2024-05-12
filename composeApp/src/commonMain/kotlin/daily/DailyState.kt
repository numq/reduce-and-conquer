package daily

import card.FlippableCard
import pokemon.Pokemon

data class DailyState(
    val maxAttributeValue: Int? = null,
    val card: FlippableCard<Pokemon>? = null,
)