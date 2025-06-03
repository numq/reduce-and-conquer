package daily

import card.FlippableCard
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import pokemon.PokemonProvider
import pokemon.toPokemon
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class DailyFeatureTest {
    private companion object {
        const val MAX_ATTRIBUTE_VALUE = 255
        val pokemon = PokemonProvider.randomPokemonJson().toPokemon()
    }

    private val testDispatcher = StandardTestDispatcher()

    private val testScope = TestScope(testDispatcher)

    private val getMaxAttributeValue: GetMaxAttributeValue = mockk()

    private val getDailyPokemon: GetDailyPokemon = mockk()

    private lateinit var feature: DailyFeature

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        feature = DailyFeature(
            coroutineScope = testScope,
            reducer = DailyReducer(getMaxAttributeValue = getMaxAttributeValue, getDailyPokemon = getDailyPokemon)
        )

        coEvery { getMaxAttributeValue.execute(Unit) } coAnswers {
            Result.success(MAX_ATTRIBUTE_VALUE)
        }

        coEvery { getDailyPokemon.execute(Unit) } coAnswers {
            Result.success(pokemon)
        }
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()

        clearAllMocks()
    }

    @Test
    fun returnMaxAttribute() = runTest {
        assertTrue(feature.execute(DailyCommand.GetMaxAttributeValue))

        advanceUntilIdle()

        assertEquals(MAX_ATTRIBUTE_VALUE, feature.state.value.maxAttributeValue)
    }

    @Test
    fun returnDailyPokemon() = runTest {
        assertTrue(feature.execute(DailyCommand.GetDailyPokemon))

        advanceUntilIdle()

        assertEquals(FlippableCard(item = pokemon), feature.state.value.card)
    }
}