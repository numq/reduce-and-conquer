package pokemon

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class PokemonRepositoryTest {
    private val service: PokemonService = mockk()

    private lateinit var repository: PokemonRepository

    @BeforeTest
    fun beforeEach() {
        repository = PokemonRepository.Implementation(service = service)
    }

    @AfterTest
    fun afterEach() {
        clearAllMocks()
    }

    @Test
    fun returnPokemons() = runTest {
        val pokemons = arrayOfNulls<Unit>(10).map { PokemonProvider.randomPokemonJson() }

        coEvery { service.getPokemonImageById(any()) } returns Result.success(byteArrayOf())
        coEvery { service.getPokemons() } returns Result.success(pokemons)

        assertContentEquals(
            pokemons.map(PokemonJson::toPokemon).map { it.copy(imageBytes = byteArrayOf()) },
            repository.getPokemons().getOrThrow()
        )
    }

    @Test
    fun returnAttributeRanges() = runTest {
        val minAttribute = 0
        val maxAttribute = 255
        val pokemons = listOf(
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

        coEvery { service.getPokemonImageById(any()) } returns Result.success(byteArrayOf())
        coEvery { service.getPokemons() } returns Result.success(pokemons)

        assertEquals(
            Pokemon.Attribute.Kind.entries.associateWith { (0..255) },
            repository.getAttributeRanges().getOrThrow()
        )
    }
}