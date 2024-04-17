package pokemon

interface PokemonRepository {
    suspend fun getPokemons(): Result<List<Pokemon>>
    suspend fun getAttributeRanges(): Result<Map<Pokemon.Attribute.Kind, IntRange>>

    class Implementation(private val service: PokemonService) : PokemonRepository {
        private var pokemons = emptyList<Pokemon>()

        private var attributeRanges = emptyMap<Pokemon.Attribute.Kind, IntRange>()

        override suspend fun getPokemons() = if (pokemons.isNotEmpty()) {
            Result.success(pokemons)
        } else {
            service.getPokemons().map { pokemons ->
                pokemons.map(PokemonJson::toPokemon).map { pokemon ->
                    pokemon.copy(imageBytes = service.getPokemonImageById(pokemon.id).getOrNull())
                }
            }.onSuccess { pokemons ->
                this.pokemons = pokemons
            }
        }

        override suspend fun getAttributeRanges() = if (attributeRanges.isNotEmpty()) {
            Result.success(attributeRanges)
        } else {
            getPokemons().map { pokemons ->
                val minHp = pokemons.minBy { pokemon ->
                    pokemon.attributes.hp.value
                }.attributes.hp.value

                val maxHp = pokemons.maxBy { pokemon ->
                    pokemon.attributes.hp.value
                }.attributes.hp.value

                val minSpeed = pokemons.minBy { pokemon ->
                    pokemon.attributes.speed.value
                }.attributes.speed.value

                val maxSpeed = pokemons.maxBy { pokemon ->
                    pokemon.attributes.speed.value
                }.attributes.speed.value

                val minBasicAttack = pokemons.minBy { pokemon ->
                    pokemon.attributes.basicAttack.value
                }.attributes.basicAttack.value

                val maxBasicAttack = pokemons.maxBy { pokemon ->
                    pokemon.attributes.basicAttack.value
                }.attributes.basicAttack.value

                val minBasicDefense = pokemons.minBy { pokemon ->
                    pokemon.attributes.basicDefense.value
                }.attributes.basicDefense.value

                val maxBasicDefense = pokemons.maxBy { pokemon ->
                    pokemon.attributes.basicDefense.value
                }.attributes.basicDefense.value

                val minSpecialAttack = pokemons.minBy { pokemon ->
                    pokemon.attributes.specialAttack.value
                }.attributes.specialAttack.value

                val maxSpecialAttack = pokemons.maxBy { pokemon ->
                    pokemon.attributes.specialAttack.value
                }.attributes.specialAttack.value

                val minSpecialDefense = pokemons.minBy { pokemon ->
                    pokemon.attributes.specialDefense.value
                }.attributes.specialDefense.value

                val maxSpecialDefense = pokemons.maxBy { pokemon ->
                    pokemon.attributes.specialDefense.value
                }.attributes.specialDefense.value

                mapOf(
                    Pokemon.Attribute.Kind.HP to IntRange(minHp, maxHp),
                    Pokemon.Attribute.Kind.SPEED to IntRange(minSpeed, maxSpeed),
                    Pokemon.Attribute.Kind.BASIC_ATTACK to IntRange(minBasicAttack, maxBasicAttack),
                    Pokemon.Attribute.Kind.BASIC_DEFENSE to IntRange(minBasicDefense, maxBasicDefense),
                    Pokemon.Attribute.Kind.SPECIAL_ATTACK to IntRange(minSpecialAttack, maxSpecialAttack),
                    Pokemon.Attribute.Kind.SPECIAL_DEFENSE to IntRange(minSpecialDefense, maxSpecialDefense)
                )
            }
        }
    }
}