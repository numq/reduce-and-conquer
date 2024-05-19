package pokedex

import card.FlippableCard
import daily.GetMaxAttributeValue
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
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

class PokedexFeatureTest {
    private companion object {
        const val MAX_ATTRIBUTE_VALUE = 255
        val pokemons = arrayOfNulls<Unit>(10).map { PokemonProvider.randomPokemonJson().toPokemon() }
        val cards = pokemons.map { pokemon -> FlippableCard(item = pokemon) }
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

    private val cardsReducer = CardsReducer(
        getMaxAttributeValue = getMaxAttributeValue,
        getPokemons = getPokemons
    )

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
        cardsReducer = cardsReducer,
        changeSort = changeSort
    )

    private val pokedexReducer = PokedexReducer(
        cardsReducer = cardsReducer,
        filterReducer = filterReducer,
        sortReducer = sortReducer
    )

    private lateinit var feature: PokedexFeature

    @BeforeTest
    fun beforeEach() {
        feature = PokedexFeature(reducer = pokedexReducer)
    }

    @AfterTest
    fun afterEach() {
        clearAllMocks()
    }

    @Test
    fun returnMaxAttributeValue() = runTest {
        coEvery { getMaxAttributeValue.execute(Unit) } returns Result.success(MAX_ATTRIBUTE_VALUE)

        assertTrue(feature.execute(PokedexCommand.Cards.GetMaxAttributeValue))

        assertEquals(MAX_ATTRIBUTE_VALUE, feature.state.value.maxAttributeValue)
    }

    @Test
    fun returnPokemons() = runTest {
        coEvery { getFilters.execute(Unit) } returns Result.success(emptyList())

        coEvery { getPokemons.execute(any()) } returns Result.success(pokemons)

        assertTrue(feature.execute(PokedexCommand.Cards.GetCards(0, pokemons.size.toLong())))

        assertEquals(cards, feature.state.value.cards)
    }

    @Test
    fun loadMorePokemons() = runTest {
        coEvery { getPokemons.execute(any()) } returns Result.success(pokemons)

        assertTrue(feature.execute(PokedexCommand.Cards.LoadMoreCards))

        assertEquals(cards, feature.state.value.cards)
    }

    @Test
    fun initializeFilters() = runTest {
        coEvery { initializeFilters.execute(Unit) } returns Result.success(Unit)

        assertTrue(feature.execute(PokedexCommand.Filter.InitializeFilters))

        assertEquals(emptyList(), feature.state.value.filters)
    }

    @Test
    fun toggleFilterMode() = runTest {
        assertTrue(feature.execute(PokedexCommand.Filter.ToggleFilterMode))

        assertEquals(PokedexInteractionMode.FILTER, feature.state.value.interactionMode)

        assertTrue(feature.execute(PokedexCommand.Filter.ToggleFilterMode))

        assertEquals(PokedexInteractionMode.NONE, feature.state.value.interactionMode)
    }

    @Test
    fun selectFilter() = runTest {
        val criteria = PokedexFilter.Criteria.entries.filter {
            it.name in Pokemon.Attribute.Kind.entries.map { kind -> kind.name }
        }.random()

        val filter = PokedexFilter.Attribute(
            criteria = criteria,
            kind = Pokemon.Attribute.Kind.valueOf(criteria.name),
            default = 1..100
        )

        coEvery { selectFilter.execute(any()) } answers { Result.success(filter) }

        assertTrue(feature.execute(PokedexCommand.Filter.SelectFilter(criteria)))

        assertEquals(criteria, feature.state.value.selectedFilter?.criteria)
    }

    @Test
    fun filterByName() = runTest {
        val filter = PokedexFilter.Name("default")

        val updatedFilter = filter.copy(modified = "modified")

        coEvery { initializeFilters.execute(Unit) } returns Result.success(Unit)

        coEvery { getPokemons.execute(any()) } returns Result.success(pokemons)

        coEvery { getFilters.execute(Unit) } answers { Result.success(listOf(filter)) }

        coEvery { updateFilter.execute(any()) } answers { Result.success(updatedFilter) }

        assertTrue(feature.execute(PokedexCommand.Filter.InitializeFilters))

        assertTrue(feature.execute(PokedexCommand.Filter.UpdateFilter(updatedFilter)))

        assertFalse(feature.state.value.isFiltered)

        assertEquals(listOf(updatedFilter), feature.state.value.filters)
    }

    @Test
    fun filterByType() = runTest {
        val filter = PokedexFilter.Type(emptySet())

        val updatedFilter = filter.copy(modified = setOf(Pokemon.Type.ICE, Pokemon.Type.FIRE, Pokemon.Type.WATER))

        coEvery { initializeFilters.execute(Unit) } returns Result.success(Unit)

        coEvery { getPokemons.execute(any()) } returns Result.success(pokemons)

        coEvery { getFilters.execute(Unit) } answers { Result.success(listOf(filter)) }

        coEvery { updateFilter.execute(any()) } answers { Result.success(updatedFilter) }

        assertTrue(feature.execute(PokedexCommand.Filter.InitializeFilters))

        assertTrue(feature.execute(PokedexCommand.Filter.UpdateFilter(updatedFilter)))

        assertTrue(feature.state.value.isFiltered)

        assertEquals(listOf(updatedFilter), feature.state.value.filters)
    }

    @Test
    fun filterByAttribute() = runTest {
        val filter = PokedexFilter.Attribute(
            criteria = PokedexFilter.Criteria.HP,
            kind = Pokemon.Attribute.Kind.HP,
            default = 1..100
        )

        val updatedFilter = filter.copy(modified = 25..50)

        coEvery { initializeFilters.execute(Unit) } returns Result.success(Unit)

        coEvery { getPokemons.execute(any()) } returns Result.success(pokemons)

        coEvery { getFilters.execute(Unit) } answers { Result.success(listOf(filter)) }

        coEvery { updateFilter.execute(any()) } answers { Result.success(updatedFilter) }

        assertTrue(feature.execute(PokedexCommand.Filter.InitializeFilters))

        assertTrue(feature.execute(PokedexCommand.Filter.UpdateFilter(updatedFilter)))

        assertTrue(feature.state.value.isFiltered)

        assertEquals(listOf(updatedFilter), feature.state.value.filters)
    }

    @Test
    fun resetFilter() = runTest {
        val filter = PokedexFilter.Attribute(
            criteria = PokedexFilter.Criteria.HP,
            kind = Pokemon.Attribute.Kind.HP,
            default = 1..100
        )

        val updatedFilter = filter.copy(modified = (25..50))

        coEvery { getPokemons.execute(any()) } returns Result.success(pokemons)

        coEvery { getFilters.execute(Unit) } answers { Result.success(emptyList()) }

        coEvery { selectFilter.execute(any()) } answers { Result.success(filter) }

        coEvery { updateFilter.execute(any()) } answers { Result.success(firstArg()) }

        coEvery { resetFilter.execute(any()) } answers { Result.success(firstArg()) }

        assertTrue(feature.execute(PokedexCommand.Filter.SelectFilter(filter.criteria)))

        assertTrue(feature.execute(PokedexCommand.Filter.UpdateFilter(updatedFilter)))

        assertTrue(feature.execute(PokedexCommand.Filter.ResetFilter(filter.criteria)))

        assertTrue(feature.execute(PokedexCommand.Filter.CloseFilter))

        assertFalse(feature.state.value.isFiltered)

        assertNull(feature.state.value.selectedFilter)

        assertEquals(emptyList(), feature.state.value.filters)
    }

    @Test
    fun closeFilter() = runTest {
        val filter = PokedexFilter.Attribute(
            criteria = PokedexFilter.Criteria.HP,
            kind = Pokemon.Attribute.Kind.HP,
            default = 1..100
        )

        coEvery { selectFilter.execute(any()) } answers { Result.success(filter) }

        assertTrue(feature.execute(PokedexCommand.Filter.SelectFilter(filter.criteria)))

        assertTrue(feature.execute(PokedexCommand.Filter.CloseFilter))

        assertEquals(null, feature.state.value.selectedFilter)
    }

    @Test
    fun resetFilters() = runTest {
        val filter = PokedexFilter.Attribute(
            criteria = PokedexFilter.Criteria.HP,
            kind = Pokemon.Attribute.Kind.HP,
            default = 1..100
        )

        coEvery { selectFilter.execute(any()) } answers { Result.success(filter) }

        assertTrue(feature.execute(PokedexCommand.Filter.ResetFilters))

        assertEquals(emptyList(), feature.state.value.filters)

        assertFalse(feature.state.value.isFiltered)

        assertNull(feature.state.value.selectedFilter)
    }

    @Test
    fun toggleSortMode() = runTest {
        assertTrue(feature.execute(PokedexCommand.Sort.ToggleSortMode))

        assertEquals(PokedexInteractionMode.SORT, feature.state.value.interactionMode)

        assertTrue(feature.execute(PokedexCommand.Sort.ToggleSortMode))

        assertEquals(PokedexInteractionMode.NONE, feature.state.value.interactionMode)
    }

    @Test
    fun sortPokemons() = runTest {
        val sort = PokedexSort(criteria = PokedexSort.Criteria.HP, isAscending = true)

        coEvery { getPokemons.execute(any()) } returns Result.success(pokemons)

        coEvery { getFilters.execute(Unit) } answers { Result.success(emptyList()) }

        coEvery { changeSort.execute(any()) } coAnswers { Result.success(firstArg()) }

        assertTrue(feature.execute(PokedexCommand.Sort.SortPokemons(sort)))

        assertEquals(cards, feature.state.value.cards)
    }
}