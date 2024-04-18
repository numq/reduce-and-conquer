package pokemon.card

sealed class PokemonCardSide private constructor(val angle: Float) {
    data object Front : PokemonCardSide(0f)
    data object Back : PokemonCardSide(180f)

    fun flip() = when (this) {
        is Front -> Back

        is Back -> Front
    }
}