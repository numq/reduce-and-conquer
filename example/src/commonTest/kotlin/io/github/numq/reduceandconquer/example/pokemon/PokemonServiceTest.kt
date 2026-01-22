package io.github.numq.reduceandconquer.example.pokemon

import io.github.numq.reduceandconquer.example.file.FileProvider
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class PokemonServiceTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun returnPokemons() = runTest {
        val pokemons = List(10) { PokemonProvider.randomPokemonJson() }
        val jsonString = json.encodeToString(pokemons)
        val bytes = jsonString.encodeToByteArray()

        val stubFileProvider = object : FileProvider {
            override suspend fun open(path: String) = Result.success(bytes)
        }

        val serviceWithStub = PokemonService.Implementation(fileProvider = stubFileProvider, json = json)

        val result = serviceWithStub.pokemons.lastOrNull()

        assertEquals(pokemons.size, result?.size)
        assertEquals(pokemons.first().id, result?.first()?.id)
    }
}