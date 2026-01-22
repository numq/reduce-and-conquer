package io.github.numq.reduceandconquer.example.navigation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class NavigationFeatureTest {
    private val testDispatcher = StandardTestDispatcher()

    private val testScope = TestScope(testDispatcher)

    private lateinit var feature: NavigationFeature

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        feature = NavigationFeature(
            initialState = NavigationState.Daily, scope = testScope, reducer = NavigationReducer()
        )
    }

    @AfterTest
    fun tearDown() {
        feature.close()
        Dispatchers.resetMain()
    }

    @Test
    fun changeDestination() = runTest(testDispatcher) {
        assertIs<NavigationState.Daily>(feature.state.value)

        feature.execute(NavigationCommand.NavigateToPokedex)
        advanceUntilIdle()
        assertIs<NavigationState.Pokedex>(feature.state.value)

        feature.execute(NavigationCommand.NavigateToDaily)
        advanceUntilIdle()
        assertIs<NavigationState.Daily>(feature.state.value)
    }
}