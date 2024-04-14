package pokemon

import file.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.serialization.json.Json

interface PokemonRepository {
    suspend fun getPokemons(): Result<List<Pokemon>>
    suspend fun getAttributeRanges(): Result<Map<Pokemon.Attribute.Kind, IntRange>>

    class Implementation(
        private val fileProvider: FileProvider,
        private val jsonParser: Json,
    ) : PokemonRepository {
        private val coroutineScope = CoroutineScope(Dispatchers.Default)

        private val pokemons by lazy {
            coroutineScope.async {
                fileProvider.open("files/pokedex.json").map(ByteArray::decodeToString).map { json ->
                    jsonParser.decodeFromString<Array<PokemonJson>>(json)
                }.map { pokemons ->
                    pokemons.map(PokemonJson::toPokemon).map { pokemon ->
                        val imageName = "${pokemon.id}".padStart(3, '0')
                        val imageBytes = fileProvider.open("drawable/pokemon/$imageName.png").getOrThrow()
                        pokemon.copy(imageBytes = imageBytes)
                    }
                }
            }
        }

        private val attributeRanges by lazy {
            coroutineScope.async {
                pokemons.await().mapCatching { pokemons ->
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

                    mutableMapOf(
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

        override suspend fun getPokemons() = pokemons.await()

        override suspend fun getAttributeRanges() = attributeRanges.await()
    }
}