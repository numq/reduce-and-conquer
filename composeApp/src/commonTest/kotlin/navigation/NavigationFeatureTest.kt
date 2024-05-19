package navigation

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertTrue

class NavigationFeatureTest {
    @Test
    fun changeDestination() = runTest {
        val reducer = NavigationReducer()

        val feature = NavigationFeature(reducer)

        assertTrue(feature.execute(NavigationCommand.NavigateToPokedex))

        assertIs<NavigationState.Pokedex>(feature.state.value)

        assertTrue(feature.execute(NavigationCommand.NavigateToDaily))

        assertIs<NavigationState.Daily>(feature.state.value)
    }
}