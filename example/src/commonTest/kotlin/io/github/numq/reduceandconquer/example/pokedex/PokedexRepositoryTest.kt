package io.github.numq.reduceandconquer.example.pokedex

import io.github.numq.reduceandconquer.example.pokedex.filter.PokedexFilter
import io.github.numq.reduceandconquer.example.pokemon.Pokemon
import io.github.numq.reduceandconquer.example.pokemon.PokemonJson
import io.github.numq.reduceandconquer.example.pokemon.PokemonService
import io.github.numq.reduceandconquer.example.pokemon.toJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class PokedexRepositoryTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: PokedexRepository.Implementation

    private val pokemons = listOf(
        Pokemon(
            id = 1,
            name = "P1",
            attributes = mockAttributes(hp = 100),
            types = setOf(Pokemon.Type.FIRE),
            imagePath = null
        ), Pokemon(
            id = 2,
            name = "P2",
            attributes = mockAttributes(hp = 200),
            types = setOf(Pokemon.Type.WATER),
            imagePath = null
        )
    )

    private class PokemonServiceStub(list: List<PokemonJson>) : PokemonService {
        override val pokemons = flowOf(list)

        override suspend fun getPokemonImagePath(id: Int) = Result.success("")
    }

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        val stub = PokemonServiceStub(pokemons.map(Pokemon::toJson))

        repository = PokedexRepository.Implementation(stub, testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        repository.close()
        Dispatchers.resetMain()
    }

    private fun mockAttributes(hp: Int) = Pokemon.Attributes(
        hp = Pokemon.Attribute(Pokemon.Attribute.Kind.HP, hp),
        speed = Pokemon.Attribute(Pokemon.Attribute.Kind.SPEED, 50),
        basicAttack = Pokemon.Attribute(Pokemon.Attribute.Kind.BASIC_ATTACK, 50),
        basicDefense = Pokemon.Attribute(Pokemon.Attribute.Kind.BASIC_DEFENSE, 50),
        specialAttack = Pokemon.Attribute(Pokemon.Attribute.Kind.SPECIAL_ATTACK, 50),
        specialDefense = Pokemon.Attribute(Pokemon.Attribute.Kind.SPECIAL_DEFENSE, 50)
    )

    @Test
    fun `initialization calculates correct ranges`() = runTest(testDispatcher) {
        val job = backgroundScope.launch { repository.pokedex.collect() }
        advanceUntilIdle()

        val pokedex = repository.pokedex.value
        assertEquals(100..200, pokedex.attributeRanges[Pokemon.Attribute.Kind.HP])
        assertNotNull(pokedex.dailyPokemon)

        job.cancel()
    }

    @Test
    fun `updateFilter correctly filters pokemon list`() = runTest(testDispatcher) {
        val job = backgroundScope.launch { repository.pokedex.collect() }
        advanceUntilIdle()

        repository.updateFilter(PokedexFilter.Name(default = "P1"))
        advanceUntilIdle()

        val filteredList = repository.pokedex.value.pokemons
        assertEquals(1, filteredList.size)
        assertEquals(1, filteredList.first().id)

        job.cancel()
    }

    @Test
    fun `resetFilters restores initial state`() = runTest(testDispatcher) {
        val job = launch { repository.pokedex.collect() }
        advanceUntilIdle()

        val nameFilter = repository.pokedex.value.filters[PokedexFilter.Criteria.NAME] as PokedexFilter.Name
        val modifiedFilter = nameFilter.copy(modified = "NonExistent")

        repository.updateFilter(modifiedFilter)
        advanceUntilIdle()

        assertEquals(0, repository.pokedex.value.pokemons.size)

        repository.resetFilters()
        advanceUntilIdle()

        assertEquals(2, repository.pokedex.value.pokemons.size)

        job.cancel()
    }
}