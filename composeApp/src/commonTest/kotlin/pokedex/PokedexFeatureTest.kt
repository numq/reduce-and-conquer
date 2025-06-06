package pokedex

import card.FlippableCard
import daily.GetMaxAttributeValue
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import pokedex.filter.*
import pokedex.presentation.*
import pokedex.presentation.filter.FilterReducer
import pokedex.presentation.sort.SortReducer
import pokedex.sort.ChangeSort
import pokedex.sort.PokedexSort
import pokemon.Pokemon
import pokemon.PokemonProvider
import pokemon.toPokemon
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class PokedexFeatureTest {
    private companion object {
        const val MAX_ATTRIBUTE_VALUE = 255
        val pokemons = arrayOfNulls<Unit>(10).map { PokemonProvider.randomPokemonJson().toPokemon() }
        val cards = pokemons.map { pokemon -> FlippableCard(item = pokemon) }
    }

    private val testDispatcher = StandardTestDispatcher()

    private val testScope = TestScope(testDispatcher)

    private val getMaxAttributeValue: GetMaxAttributeValue = mockk()

    private val initializeFilters: InitializeFilters = mockk()

    private val getPokemons: GetPokemons = mockk()

    private val getFilters: GetFilters = mockk()

    private val selectFilter: SelectFilter = mockk()

    private val updateFilter: UpdateFilter = mockk()

    private val resetFilter: ResetFilter = mockk()

    private val resetFilters: ResetFilters = mockk()

    private val changeSort: ChangeSort = mockk()

    private val cardsReducer = CardsReducer(getMaxAttributeValue = getMaxAttributeValue, getPokemons = getPokemons)

    private val filterReducer = FilterReducer(
        cardsReducer = cardsReducer,
        initializeFilters = initializeFilters,
        getFilters = getFilters,
        selectFilter = selectFilter,
        updateFilter = updateFilter,
        resetFilter = resetFilter,
        resetFilters = resetFilters
    )

    private val sortReducer = SortReducer(
        cardsReducer = cardsReducer, changeSort = changeSort
    )

    private val pokedexReducer = PokedexReducer(
        cardsReducer = cardsReducer, filterReducer = filterReducer, sortReducer = sortReducer
    )

    private lateinit var feature: PokedexFeature

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        coEvery { getMaxAttributeValue.execute(Unit) } coAnswers { Result.success(MAX_ATTRIBUTE_VALUE) }

        coEvery { initializeFilters.execute(Unit) } coAnswers { Result.success(Unit) }

        coEvery { getPokemons.execute(any()) } coAnswers { Result.success(pokemons) }

        feature = PokedexFeature(coroutineScope = testScope, reducer = pokedexReducer)
    }

    @AfterTest
    fun tearDown() {
        feature.close()

        Dispatchers.resetMain()

        clearAllMocks()
    }

    @Test
    fun returnMaxAttributeValue() = runTest {
        feature.execute(PokedexCommand.Cards.GetMaxAttributeValue)

        advanceUntilIdle()

        assertEquals(MAX_ATTRIBUTE_VALUE, feature.state.value.maxAttributeValue)
    }

    @Test
    fun returnPokemons() = runTest {
        coEvery { getFilters.execute(Unit) } coAnswers { Result.success(emptyList()) }

        feature.execute(PokedexCommand.Cards.GetCards(0, pokemons.size.toLong()))

        advanceUntilIdle()

        assertEquals(cards, feature.state.value.cards)
    }

    @Test
    fun loadMorePokemons() = runTest {
        feature.execute(PokedexCommand.Cards.LoadMoreCards)

        advanceUntilIdle()

        assertEquals(cards, feature.state.value.cards)
    }

    @Test
    fun initializeFilters() = runTest {
        feature.execute(PokedexCommand.Filter.InitializeFilters)

        advanceUntilIdle()

        assertEquals(emptyList(), feature.state.value.filters)
    }

    @Test
    fun toggleFilterMode() = runTest {
        feature.execute(PokedexCommand.Filter.ToggleFilterMode)

        advanceUntilIdle()

        assertEquals(PokedexInteractionMode.FILTER, feature.state.value.interactionMode)

        feature.execute(PokedexCommand.Filter.ToggleFilterMode)

        advanceUntilIdle()

        assertEquals(PokedexInteractionMode.NONE, feature.state.value.interactionMode)
    }

    @Test
    fun selectFilter() = runTest {
        val criteria = PokedexFilter.Criteria.entries.filter {
            it.name in Pokemon.Attribute.Kind.entries.map { kind -> kind.name }
        }.random()

        val filter = PokedexFilter.Attribute(
            criteria = criteria, kind = Pokemon.Attribute.Kind.valueOf(criteria.name), default = 1..100
        )

        coEvery { selectFilter.execute(any()) } coAnswers { Result.success(filter) }

        feature.execute(PokedexCommand.Filter.SelectFilter(criteria))

        advanceUntilIdle()

        assertEquals(criteria, feature.state.value.selectedFilter?.criteria)
    }

    @Test
    fun filterByName() = runTest {
        val filter = PokedexFilter.Name("default")

        val updatedFilter = filter.copy(modified = "modified")

        coEvery { getFilters.execute(Unit) } coAnswers { Result.success(listOf(filter)) }

        coEvery { updateFilter.execute(any()) } coAnswers { Result.success(updatedFilter) }

        feature.execute(PokedexCommand.Filter.InitializeFilters)

        advanceUntilIdle()

        feature.execute(PokedexCommand.Filter.UpdateFilter(updatedFilter))

        advanceUntilIdle()

        assertFalse(feature.state.value.isFiltered)

        assertEquals(listOf(updatedFilter), feature.state.value.filters)
    }

    @Test
    fun filterByType() = runTest {
        val filter = PokedexFilter.Type(emptySet())

        val updatedFilter = filter.copy(modified = setOf(Pokemon.Type.ICE, Pokemon.Type.FIRE, Pokemon.Type.WATER))

        coEvery { getFilters.execute(Unit) } coAnswers { Result.success(listOf(filter)) }

        coEvery { updateFilter.execute(any()) } coAnswers { Result.success(updatedFilter) }

        feature.execute(PokedexCommand.Filter.InitializeFilters)

        advanceUntilIdle()

        feature.execute(PokedexCommand.Filter.UpdateFilter(updatedFilter))

        advanceUntilIdle()

        assertTrue(feature.state.value.isFiltered)

        assertEquals(listOf(updatedFilter), feature.state.value.filters)
    }

    @Test
    fun filterByAttribute() = runTest {
        val filter = PokedexFilter.Attribute(
            criteria = PokedexFilter.Criteria.HP, kind = Pokemon.Attribute.Kind.HP, default = 1..100
        )

        val updatedFilter = filter.copy(modified = 25..50)

        coEvery { getFilters.execute(Unit) } coAnswers { Result.success(listOf(filter)) }

        coEvery { updateFilter.execute(any()) } coAnswers { Result.success(updatedFilter) }

        feature.execute(PokedexCommand.Filter.InitializeFilters)

        advanceUntilIdle()

        feature.execute(PokedexCommand.Filter.UpdateFilter(updatedFilter))

        advanceUntilIdle()

        assertTrue(feature.state.value.isFiltered)

        assertEquals(listOf(updatedFilter), feature.state.value.filters)
    }

    @Test
    fun resetFilter() = runTest {
        val filter = PokedexFilter.Attribute(
            criteria = PokedexFilter.Criteria.HP, kind = Pokemon.Attribute.Kind.HP, default = 1..100
        )

        val updatedFilter = filter.copy(modified = (25..50))

        coEvery { getFilters.execute(Unit) } coAnswers { Result.success(emptyList()) }

        coEvery { selectFilter.execute(any()) } coAnswers { Result.success(filter) }

        coEvery { updateFilter.execute(any()) } coAnswers { Result.success(firstArg()) }

        coEvery { resetFilter.execute(any()) } coAnswers { Result.success(firstArg()) }

        feature.execute(PokedexCommand.Filter.SelectFilter(filter.criteria))

        advanceUntilIdle()

        feature.execute(PokedexCommand.Filter.UpdateFilter(updatedFilter))

        advanceUntilIdle()

        feature.execute(PokedexCommand.Filter.ResetFilter(filter.criteria))

        advanceUntilIdle()

        feature.execute(PokedexCommand.Filter.CloseFilter)

        advanceUntilIdle()

        assertFalse(feature.state.value.isFiltered)

        assertNull(feature.state.value.selectedFilter)

        assertEquals(emptyList(), feature.state.value.filters)
    }

    @Test
    fun closeFilter() = runTest {
        val filter = PokedexFilter.Attribute(
            criteria = PokedexFilter.Criteria.HP, kind = Pokemon.Attribute.Kind.HP, default = 1..100
        )

        coEvery { selectFilter.execute(any()) } coAnswers { Result.success(filter) }

        feature.execute(PokedexCommand.Filter.SelectFilter(filter.criteria))

        advanceUntilIdle()

        feature.execute(PokedexCommand.Filter.CloseFilter)

        advanceUntilIdle()

        assertEquals(null, feature.state.value.selectedFilter)
    }

    @Test
    fun resetFilters() = runTest {
        val filter = PokedexFilter.Attribute(
            criteria = PokedexFilter.Criteria.HP, kind = Pokemon.Attribute.Kind.HP, default = 1..100
        )

        coEvery { selectFilter.execute(any()) } coAnswers { Result.success(filter) }

        feature.execute(PokedexCommand.Filter.ResetFilters)

        advanceUntilIdle()

        assertEquals(emptyList(), feature.state.value.filters)

        assertFalse(feature.state.value.isFiltered)

        assertNull(feature.state.value.selectedFilter)
    }

    @Test
    fun toggleSortMode() = runTest {
        feature.execute(PokedexCommand.Sort.ToggleSortMode)

        advanceUntilIdle()

        assertEquals(PokedexInteractionMode.SORT, feature.state.value.interactionMode)

        feature.execute(PokedexCommand.Sort.ToggleSortMode)

        advanceUntilIdle()

        assertEquals(PokedexInteractionMode.NONE, feature.state.value.interactionMode)
    }

    @Test
    fun sortPokemons() = runTest {
        val sort = PokedexSort(criteria = PokedexSort.Criteria.HP, isAscending = true)

        coEvery { getFilters.execute(Unit) } coAnswers { Result.success(emptyList()) }

        coEvery { changeSort.execute(any()) } coAnswers { Result.success(firstArg()) }

        feature.execute(PokedexCommand.Sort.SortPokemons(sort))

        advanceUntilIdle()

        assertEquals(cards, feature.state.value.cards)
    }
}