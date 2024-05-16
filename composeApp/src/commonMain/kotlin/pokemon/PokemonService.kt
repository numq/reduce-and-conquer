package pokemon

import file.FileProvider
import kotlinx.serialization.json.Json

interface PokemonService {
    suspend fun getPokemons(): Result<List<PokemonJson>>
    suspend fun getPokemonImageById(id: Int): Result<ByteArray>

    class Implementation(
        private val fileProvider: FileProvider,
        private val jsonParser: Json,
    ) : PokemonService {
        companion object {
            const val POKEDEX_JSON_PATH = "files/pokedex.json"
            const val POKEMON_IMAGE_PATH = "drawable/pokemon/%s.png"
        }

        override suspend fun getPokemons() = fileProvider.open(POKEDEX_JSON_PATH)
            .mapCatching(ByteArray::decodeToString)
            .mapCatching<Array<PokemonJson>, String>(jsonParser::decodeFromString)
            .mapCatching(Array<PokemonJson>::toList)

        override suspend fun getPokemonImageById(id: Int) = "$id".padStart(3, '0').let { imageName ->
            fileProvider.open(POKEMON_IMAGE_PATH.replace("%s", imageName))
        }
    }
}