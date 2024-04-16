package pokedex

import daily.GetMaxAttributeValue
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import pokedex.filter.*
import pokedex.presentation.PokedexFeature
import pokedex.presentation.PokedexInteractionMode
import pokedex.presentation.PokedexMessage
import pokedex.presentation.PokedexState
import pokedex.sort.ChangeSort
import pokedex.sort.PokedexSort
import pokemon.PokemonProvider
import kotlin.test.*

class PokedexFeatureTest {
    private companion object {
        const val MAX_ATTRIBUTE_VALUE = 255
        val pokemons = arrayOfNulls<Unit>(10).map { PokemonProvider.randomPokemon() }
        val filter = PokedexFilter.Name("default")
        val filters = listOf(filter)
    }

    private val getMaxAttributeValue: GetMaxAttributeValue = mockk()
    private val initializeFilters: InitializeFilters = mockk()
    private val getPokemons: GetPokemons = mockk()
    private val getFilters: GetFilters = mockk()
    private val selectFilter: SelectFilter = mockk()
    private val updateFilter: UpdateFilter = mockk()
    private val resetFilter: ResetFilter = mockk()
    private val resetFilters: ResetFilters = mockk()
    private val changeSort: ChangeSort = mockk()
    private lateinit var feature: PokedexFeature

    private fun createFeature(coroutineScope: CoroutineScope, initialState: PokedexState = PokedexState()) =
        PokedexFeature(
            getMaxAttributeValue = getMaxAttributeValue,
            getPokemons = getPokemons,
            initializeFilters = initializeFilters,
            getFilters = getFilters,
            selectFilter = selectFilter,
            updateFilter = updateFilter,
            resetFilter = resetFilter,
            resetFilters = resetFilters,
            changeSort = changeSort,
            initialState = initialState,
            coroutineScope = coroutineScope
        )

    @BeforeTest
    fun beforeEach() {
        coEvery { getMaxAttributeValue.execute(Unit) } returns Result.success(MAX_ATTRIBUTE_VALUE)
        coEvery { initializeFilters.execute(Unit) } returns Result.success(Unit)
        coEvery { getPokemons.execute(any()) } returns Result.success(pokemons)
        coEvery { getFilters.execute(Unit) } returns Result.success(filters)
        coEvery { selectFilter.execute(any()) } answers { Result.success(filter) }
        coEvery { updateFilter.execute(any()) } answers { Result.success(firstArg()) }
        coEvery { resetFilter.execute(any()) } answers { Result.success(filter) }
        coEvery { resetFilters.execute(Unit) } returns Result.success(Unit)
        coEvery { changeSort.execute(any()) } answers { Result.success(firstArg()) }
    }

    @AfterTest
    fun afterEach() {
        clearAllMocks()
    }

    @Test
    fun shouldGetMaxAttributeValue() = runTest {
        feature = createFeature(backgroundScope)

        assertTrue(feature.dispatchMessage(PokedexMessage.Pokemons.GetMaxAttributeValue))
        delay(100L)
        assertEquals(MAX_ATTRIBUTE_VALUE, feature.state.value.maxAttributeValue)
    }

    @Test
    fun shouldGetPokemons() = runTest {
        feature = createFeature(backgroundScope)

        assertTrue(feature.dispatchMessage(PokedexMessage.Pokemons.GetPokemons(0, pokemons.size.toLong())))
        delay(100L)
        assertEquals(pokemons, feature.state.value.pokemons)
    }

    @Test
    fun shouldLoadMorePokemons() = runTest {
        feature = createFeature(backgroundScope)

        assertTrue(feature.dispatchMessage(PokedexMessage.Pokemons.LoadMorePokemons))
        delay(100L)
        assertEquals(pokemons + pokemons, feature.state.value.pokemons)
    }

    @Test
    fun shouldInitializeFilters() = runTest {
        feature = createFeature(backgroundScope)

        assertTrue(feature.dispatchMessage(PokedexMessage.Filter.InitializeFilters))
        delay(100L)
        assertEquals(filters, feature.state.value.filters)
    }

    @Test
    fun shouldToggleFilterMode() = runTest {
        feature = createFeature(backgroundScope)

        assertTrue(feature.dispatchMessage(PokedexMessage.Filter.ToggleFilterMode))
        delay(100L)
        assertEquals(PokedexInteractionMode.FILTER, feature.state.value.interactionMode)

        assertTrue(feature.dispatchMessage(PokedexMessage.Filter.ToggleFilterMode))
        delay(100L)
        assertEquals(PokedexInteractionMode.NONE, feature.state.value.interactionMode)
    }

    @Test
    fun shouldSelectFilter() = runTest {
        feature = createFeature(backgroundScope)

        val criteria = PokedexFilter.Criteria.NAME

        assertTrue(feature.dispatchMessage(PokedexMessage.Filter.SelectFilter(criteria)))
        delay(100L)
        assertEquals(criteria, feature.state.value.selectedFilter?.criteria)
    }

    @Test
    fun shouldUpdateFilter() = runTest {
        feature = createFeature(backgroundScope)

        val filter = PokedexFilter.Name("default", "modified")

        assertTrue(feature.dispatchMessage(PokedexMessage.Filter.UpdateFilter(filter)))

        delay(100L)
        assertTrue(feature.state.value.selectedFilter?.isModified() == true)
        assertEquals(filter, feature.state.value.selectedFilter)
    }

    @Test
    fun shouldResetFilter() = runTest {
        feature = createFeature(
            backgroundScope,
            initialState = PokedexState(filters = filters, selectedFilter = filter)
        )

        val modifiedFilter = PokedexFilter.Name("default", "modified")

        assertTrue(feature.dispatchMessage(PokedexMessage.Filter.ResetFilter(modifiedFilter.criteria)))
        delay(100L)
        assertEquals(filters, feature.state.value.filters)
        assertEquals(filter, feature.state.value.selectedFilter)
        assertFalse(feature.state.value.selectedFilter?.isModified() == true)
    }

    @Test
    fun shouldCloseFilter() = runTest {
        feature = createFeature(backgroundScope)

        assertTrue(feature.dispatchMessage(PokedexMessage.Filter.CloseFilter))
        delay(100L)
        assertEquals(null, feature.state.value.selectedFilter)
    }

    @Test
    fun shouldResetFilters() = runTest {
        feature = createFeature(
            backgroundScope,
            initialState = PokedexState(filters = filters, selectedFilter = filter)
        )

        assertTrue(feature.dispatchMessage(PokedexMessage.Filter.ResetFilters))
        delay(100L)
        assertEquals(filters, feature.state.value.filters)
        assertEquals(filter, feature.state.value.selectedFilter)
        assertFalse(feature.state.value.selectedFilter?.isModified() == true)
    }

    @Test
    fun shouldToggleSortMode() = runTest {
        feature = createFeature(backgroundScope)

        assertTrue(feature.dispatchMessage(PokedexMessage.Sort.ToggleSortMode))
        delay(100L)
        assertEquals(PokedexInteractionMode.SORT, feature.state.value.interactionMode)

        assertTrue(feature.dispatchMessage(PokedexMessage.Sort.ToggleSortMode))
        delay(100L)
        assertEquals(PokedexInteractionMode.NONE, feature.state.value.interactionMode)
    }

    @Test
    fun shouldSortPokemons() = runTest {
        feature = createFeature(backgroundScope)

        val sort = PokedexSort(criteria = PokedexSort.Criteria.HP, isAscending = false)

        assertTrue(feature.dispatchMessage(PokedexMessage.Sort.SortPokemons(sort)))
        delay(100L)
        assertEquals(pokemons, feature.state.value.pokemons)
    }
}