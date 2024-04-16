package navigation

import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class NavigationFeatureTest {
    @Test
    fun shouldReduceState() = runTest {
        val feature = NavigationFeature(coroutineScope = backgroundScope)

        assertTrue(feature.dispatchMessage(NavigationMessage.NavigateToPokedex))
        delay(100L)
        assertIs<NavigationState.Pokedex>(feature.state.value)

        assertTrue(feature.dispatchMessage(NavigationMessage.NavigateToDaily))
        delay(100L)
        assertIs<NavigationState.Daily>(feature.state.value)
    }
}