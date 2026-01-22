package io.github.numq.reduceandconquer.example.pokedex

import io.github.numq.reduceandconquer.example.pokedex.filter.PokedexFilter
import io.github.numq.reduceandconquer.example.pokedex.filter.ResetFilter
import io.github.numq.reduceandconquer.example.pokedex.filter.ResetFilters
import io.github.numq.reduceandconquer.example.pokedex.filter.UpdateFilter
import io.github.numq.reduceandconquer.example.pokedex.presentation.*
import io.github.numq.reduceandconquer.example.pokedex.presentation.filter.FilterReducer
import io.github.numq.reduceandconquer.example.pokedex.presentation.sort.SortReducer
import io.github.numq.reduceandconquer.example.pokedex.sort.ChangeSort
import io.github.numq.reduceandconquer.example.pokedex.sort.PokedexSort
import io.github.numq.reduceandconquer.example.pokemon.Pokemon
import io.github.numq.reduceandconquer.example.pokemon.PokemonProvider
import io.github.numq.reduceandconquer.example.pokemon.toPokemon
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class PokedexFeatureTest {
    private companion object {
        const val MAX_ATTRIBUTE_VALUE = 255
        val POKEMON = PokemonProvider.randomPokemonJson().toPokemon()
    }

    private val repository = mockk<PokedexRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var feature: PokedexFeature

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        val getPokedex = GetPokedex(repository)
        val filterReducer = FilterReducer(
            resetFilter = ResetFilter(repository),
            resetFilters = ResetFilters(repository),
            updateFilter = UpdateFilter(repository)
        )
        val sortReducer = SortReducer(changeSort = ChangeSort(repository))

        val reducer = PokedexReducer(
            getPokedex = getPokedex, filterReducer = filterReducer, sortReducer = sortReducer
        )

        feature = PokedexFeature(
            initialState = PokedexState(), scope = testScope, reducer = reducer
        )
    }

    @AfterTest
    fun tearDown() {
        feature.close()
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `initialization success updates cards and filters`() = runTest {
        val sort = PokedexSort(PokedexSort.Criteria.HP, false)

        val mockPokedex = Pokedex(
            pokemons = listOf(POKEMON),
            maxAttributeValue = MAX_ATTRIBUTE_VALUE,
            filters = mapOf(PokedexFilter.Criteria.TYPE to PokedexFilter.Type(emptySet())),
            sort = sort
        )

        every { repository.pokedex } returns MutableStateFlow(mockPokedex)

        feature.execute(PokedexCommand.Initialize)
        advanceUntilIdle()

        val state = feature.state.value
        assertEquals(255, state.maxAttributeValue)
        assertEquals(1, state.cards.size)
        assertEquals(sort, state.sort)
    }

    @Test
    fun `toggle filter mode updates interaction mode`() = runTest {
        assertEquals(PokedexInteractionMode.NONE, feature.state.value.interactionMode)

        feature.execute(PokedexCommand.Filter.ToggleFilterMode)
        advanceUntilIdle()
        assertEquals(PokedexInteractionMode.FILTER, feature.state.value.interactionMode)

        feature.execute(PokedexCommand.Filter.ToggleFilterMode)
        advanceUntilIdle()
        assertEquals(PokedexInteractionMode.NONE, feature.state.value.interactionMode)
    }

    @Test
    fun `update filter success updates selected filter`() = runTest {
        val filter = PokedexFilter.Type(setOf(Pokemon.Type.FIRE))

        feature.execute(PokedexCommand.Filter.UpdateFilterSuccess(filter))
        advanceUntilIdle()

        assertEquals(filter, feature.state.value.selectedFilter)
    }

    @Test
    fun `sort pokemons success completes without error`() = runTest {
        val sort = PokedexSort(PokedexSort.Criteria.BASIC_ATTACK, false)

        coEvery { repository.changeSort(any()) } returns Result.success(Unit)

        feature.execute(PokedexCommand.Sort.SortPokemons(sort))
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.changeSort(sort) }
    }
}