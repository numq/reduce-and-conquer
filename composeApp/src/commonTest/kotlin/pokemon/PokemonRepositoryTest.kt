package pokemon

import file.FileProvider
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.*

class PokemonRepositoryTest {
    private val fileProvider: FileProvider = mockk()
    private val jsonParser: Json = mockk()
    private lateinit var repository: PokemonRepository

    @BeforeTest
    fun beforeEach() {
        repository = PokemonRepository.Implementation(fileProvider, jsonParser)
    }

    @AfterTest
    fun afterEach() {
        clearAllMocks()
    }

    @Test
    fun shouldReturnPokemons() = runTest {
        val pokemons = arrayOfNulls<Unit>(10).map { PokemonProvider.randomPokemonJson() }.toTypedArray()

        coEvery { fileProvider.open(any()) } returns Result.success(byteArrayOf())
        every { jsonParser.decodeFromString<Array<PokemonJson>>(string = any()) } returns pokemons
        every { jsonParser.decodeFromString<Array<PokemonJson>>(deserializer = any(), string = any()) } returns pokemons

        assertContentEquals(
            pokemons.map(PokemonJson::toPokemon).map { it.copy(imageBytes = byteArrayOf()) },
            repository.getPokemons().getOrThrow()
        )
    }

    @Test
    fun shouldAttributeRanges() = runTest {
        val minAttribute = 0
        val maxAttribute = 255
        val pokemons = arrayOf(
            PokemonJson(
                id = 0,
                name = PokemonJson.Name(english = "0"),
                types = emptyList(),
                attributes = PokemonJson.Attributes(
                    hp = minAttribute,
                    speed = minAttribute,
                    basicAttack = minAttribute,
                    basicDefense = minAttribute,
                    specialAttack = minAttribute,
                    specialDefense = minAttribute,
                )
            ),
            PokemonJson(
                id = 1,
                name = PokemonJson.Name(english = "1"),
                types = emptyList(),
                attributes = PokemonJson.Attributes(
                    hp = maxAttribute,
                    speed = maxAttribute,
                    basicAttack = maxAttribute,
                    basicDefense = maxAttribute,
                    specialAttack = maxAttribute,
                    specialDefense = maxAttribute,
                )
            )
        )

        coEvery { fileProvider.open(any()) } returns Result.success(byteArrayOf())
        every { jsonParser.decodeFromString<Array<PokemonJson>>(string = any()) } returns pokemons
        every { jsonParser.decodeFromString<Array<PokemonJson>>(deserializer = any(), string = any()) } returns pokemons

        assertEquals(
            Pokemon.Attribute.Kind.entries.associateWith { (0..255) },
            repository.getAttributeRanges().getOrThrow()
        )
    }
}