package pokemon

import file.FileProvider
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PokemonServiceTest {
    private val fileProvider: FileProvider = mockk()

    private val jsonParser: Json = mockk()

    private lateinit var service: PokemonService

    @BeforeTest
    fun beforeEach() {
        service = PokemonService.Implementation(fileProvider = fileProvider, jsonParser = jsonParser)
    }

    @AfterTest
    fun afterEach() {
        clearAllMocks()
    }

    @Test
    fun returnPokemons() = runTest {
        val pokemons = arrayOfNulls<Unit>(10).map { PokemonProvider.randomPokemonJson() }.toTypedArray()

        coEvery { fileProvider.open(any()) } returns Result.success(byteArrayOf())
        every { jsonParser.decodeFromString<Array<PokemonJson>>(string = any()) } returns pokemons
        every { jsonParser.decodeFromString<Array<PokemonJson>>(deserializer = any(), string = any()) } returns pokemons

        assertEquals(pokemons.toList(), service.getPokemons().getOrThrow())
    }
}