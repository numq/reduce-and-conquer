package pokemon

object PokemonProvider {
    fun randomPokemonJson() = (0..999).random().let { number ->
        PokemonJson(
            id = number,
            name = PokemonJson.Name(english = number.toString()),
            types = emptyList(),
            attributes = PokemonJson.Attributes(
                hp = (0..255).random(),
                speed = (0..255).random(),
                basicAttack = (0..255).random(),
                basicDefense = (0..255).random(),
                specialAttack = (0..255).random(),
                specialDefense = (0..255).random()
            )
        )
    }
}