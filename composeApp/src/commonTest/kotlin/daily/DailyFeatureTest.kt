package daily

import card.FlippableCard
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import pokemon.PokemonProvider
import pokemon.toPokemon
import kotlin.test.*

class DailyFeatureTest {
    private companion object {
        const val MAX_ATTRIBUTE_VALUE = 255
        val pokemon = PokemonProvider.randomPokemonJson().toPokemon()
    }

    private val getMaxAttributeValue: GetMaxAttributeValue = mockk()

    private val getDailyPokemon: GetDailyPokemon = mockk()

    private val reducer = DailyReducer(getMaxAttributeValue = getMaxAttributeValue, getDailyPokemon = getDailyPokemon)

    private lateinit var feature: DailyFeature

    @BeforeTest
    fun beforeEach() {
        feature = DailyFeature(reducer)
    }

    @AfterTest
    fun afterEach() {
        clearAllMocks()
    }

    @Test
    fun returnMaxAttribute() = runTest {
        coEvery { getMaxAttributeValue.execute(Unit) } returns Result.success(MAX_ATTRIBUTE_VALUE)

        assertTrue(feature.execute(DailyCommand.GetMaxAttributeValue))

        assertEquals(MAX_ATTRIBUTE_VALUE, feature.state.value.maxAttributeValue)
    }

    @Test
    fun returnDailyPokemon() = runTest {
        coEvery { getDailyPokemon.execute(Unit) } returns Result.success(pokemon)

        assertTrue(feature.execute(DailyCommand.GetDailyPokemon))

        assertEquals(FlippableCard(item = pokemon), feature.state.value.card)
    }
}