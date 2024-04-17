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
            const val pokedexJsonPath = "files/pokedex.json"
            const val pokemonImagePath = "drawable/pokemon/%s.png"
        }

        override suspend fun getPokemons() = fileProvider.open(pokedexJsonPath)
            .mapCatching(ByteArray::decodeToString)
            .mapCatching<Array<PokemonJson>, String>(jsonParser::decodeFromString)
            .mapCatching(Array<PokemonJson>::toList)

        override suspend fun getPokemonImageById(id: Int) = "$id".padStart(3, '0').let { imageName ->
            fileProvider.open(pokemonImagePath.replace("%s", imageName))
        }
    }
}