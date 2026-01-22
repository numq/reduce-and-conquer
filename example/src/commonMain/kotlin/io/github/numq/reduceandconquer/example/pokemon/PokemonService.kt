package io.github.numq.reduceandconquer.example.pokemon

import io.github.numq.reduceandconquer.example.dispatcher.ioDispatcher
import io.github.numq.reduceandconquer.example.file.FileProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json

interface PokemonService {
    val pokemons: Flow<List<PokemonJson>>

    suspend fun getPokemonImagePath(id: Int): Result<String>

    class Implementation(
        private val fileProvider: FileProvider,
        private val json: Json,
    ) : PokemonService {
        companion object {
            const val POKEDEX_JSON_PATH = "files/pokedex.json"

            const val POKEMON_IMAGE_PATH = "drawable/pokemon/%s.png"
        }

        override val pokemons = flow {
            val result = fileProvider.open(POKEDEX_JSON_PATH).mapCatching { bytes ->
                json.decodeFromString<List<PokemonJson>>(bytes.decodeToString())
            }

            emit(result.getOrThrow())
        }.flowOn(ioDispatcher)

        override suspend fun getPokemonImagePath(id: Int) = runCatching {
            "$id".padStart(3, '0').let { imageName ->
                POKEMON_IMAGE_PATH.replace("%s", imageName)
            }
        }
    }
}