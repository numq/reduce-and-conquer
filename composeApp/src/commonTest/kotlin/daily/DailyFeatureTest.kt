package daily

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import pokemon.Pokemon
import kotlin.test.*

class DailyFeatureTest {
    private companion object {
        const val MAX_ATTRIBUTE_VALUE = 255
        val pokemon = Pokemon(
            id = 0,
            name = "test",
            types = emptySet(),
            attributes = Pokemon.Attributes(
                hp = Pokemon.Attribute(Pokemon.Attribute.Kind.HP, 0),
                speed = Pokemon.Attribute(Pokemon.Attribute.Kind.SPEED, 0),
                basicAttack = Pokemon.Attribute(Pokemon.Attribute.Kind.BASIC_ATTACK, 0),
                basicDefense = Pokemon.Attribute(Pokemon.Attribute.Kind.BASIC_DEFENSE, 0),
                specialAttack = Pokemon.Attribute(Pokemon.Attribute.Kind.SPECIAL_ATTACK, 0),
                specialDefense = Pokemon.Attribute(Pokemon.Attribute.Kind.SPECIAL_DEFENSE, 0),
            ),
        )
    }

    private val getMaxAttributeValue: GetMaxAttributeValue = mockk()
    private val getDailyPokemon: GetDailyPokemon = mockk()
    private lateinit var feature: DailyFeature

    private fun createFeature(coroutineScope: CoroutineScope) = DailyFeature(
        getMaxAttributeValue = getMaxAttributeValue,
        getDailyPokemon = getDailyPokemon,
        coroutineScope = coroutineScope
    )

    @BeforeTest
    fun beforeEach() {
        coEvery { getMaxAttributeValue.execute(Unit) } returns Result.success(MAX_ATTRIBUTE_VALUE)
        coEvery { getDailyPokemon.execute(Unit) } returns Result.success(pokemon)
    }

    @AfterTest
    fun afterEach() {
        clearAllMocks()
    }

    @Test
    fun shouldGetMaxAttribute() = runTest {
        feature = createFeature(backgroundScope)

        assertTrue(feature.dispatchMessage(DailyMessage.GetMaxAttributeValue))
        delay(100L)
        assertEquals(MAX_ATTRIBUTE_VALUE, feature.state.value.maxAttributeValue)
    }

    @Test
    fun shouldGetDailyPokemon() = runTest {
        feature = createFeature(backgroundScope)

        assertTrue(feature.dispatchMessage(DailyMessage.GetDailyPokemon))
        delay(100L)
        assertEquals(pokemon, feature.state.value.pokemon)
    }
}