package navigation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class NavigationFeatureTest {
    private val testDispatcher = StandardTestDispatcher()

    private val testScope = TestScope(testDispatcher)

    private lateinit var feature: NavigationFeature

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        feature = NavigationFeature(coroutineScope = testScope, reducer = NavigationReducer())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun changeDestination() = runTest {
        assertIs<NavigationState.Daily>(feature.state.value)

        assertTrue(feature.execute(NavigationCommand.NavigateToPokedex))

        advanceUntilIdle()

        assertIs<NavigationState.Pokedex>(feature.state.value)

        assertTrue(feature.execute(NavigationCommand.NavigateToDaily))

        advanceUntilIdle()

        assertIs<NavigationState.Daily>(feature.state.value)
    }
}