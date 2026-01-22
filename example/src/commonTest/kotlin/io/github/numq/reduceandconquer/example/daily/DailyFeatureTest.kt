package io.github.numq.reduceandconquer.example.daily

import io.github.numq.reduceandconquer.example.card.FlippableCard
import io.github.numq.reduceandconquer.example.pokedex.GetPokedex
import io.github.numq.reduceandconquer.example.pokedex.Pokedex
import io.github.numq.reduceandconquer.example.pokedex.PokedexRepository
import io.github.numq.reduceandconquer.example.pokemon.PokemonProvider
import io.github.numq.reduceandconquer.example.pokemon.toPokemon
import io.mockk.clearAllMocks
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class DailyFeatureTest {
    private companion object {
        const val MAX_ATTRIBUTE_VALUE = 255
        val POKEMON = PokemonProvider.randomPokemonJson().toPokemon()
    }

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: TestScope
    private val repository = mockk<PokedexRepository>()
    private lateinit var getPokedex: GetPokedex
    private lateinit var feature: DailyFeature

    @BeforeTest
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        testScope = TestScope(testDispatcher)
        Dispatchers.setMain(testDispatcher)

        getPokedex = GetPokedex(repository)

        feature = DailyFeature(
            initialState = DailyState(), scope = testScope, reducer = DailyReducer(getPokedex = getPokedex)
        )
    }

    @AfterTest
    fun tearDown() {
        feature.close()
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun `initialization success updates state with pokedex data`() = runTest {
        val pokedex = Pokedex(dailyPokemon = POKEMON, maxAttributeValue = MAX_ATTRIBUTE_VALUE)
        every { repository.pokedex } returns MutableStateFlow(pokedex)

        feature.execute(DailyCommand.Initialize)
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.pokedex }

        val state = feature.state.value

        assertEquals(MAX_ATTRIBUTE_VALUE, state.maxAttributeValue)
        assertEquals(FlippableCard(item = POKEMON), state.card)
    }

    @Test
    fun `initialization failure emits error event`() = runTest {
        val errorMessage = "Network Error"

        every { repository.pokedex } throws Exception(errorMessage)

        val events = mutableListOf<DailyEvent.Error>()

        val job = launch { feature.events.filterIsInstance<DailyEvent.Error>().collect(events::add) }

        feature.execute(DailyCommand.Initialize)
        advanceUntilIdle()

        assertEquals(errorMessage, events.lastOrNull()?.message)

        job.cancel()
    }
}