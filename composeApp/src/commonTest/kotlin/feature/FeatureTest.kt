package feature

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.*
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class FeatureTest {
    private val testDispatcher = StandardTestDispatcher()

    private val testScope = TestScope(testDispatcher)

    private lateinit var feature: Feature<TestCommand, TestState, TestEvent>

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        feature = object : Feature<TestCommand, TestState, TestEvent>(
            initialState = TestState(0), coroutineScope = testScope, reducer = TestReducer()
        ) {}
    }

    @AfterTest
    fun tearDown() {
        feature.close()

        Dispatchers.resetMain()
    }

    @Test
    fun updateStateAndEmitEvent() = runTest {
        val states = mutableListOf<TestState>()

        feature.state.onEach { state ->
            states.add(state)
        }.launchIn(testScope)

        advanceUntilIdle()

        val events = mutableListOf<TestEvent>()

        feature.events.onEach { event ->
            events.add(event)
        }.launchIn(testScope)

        advanceUntilIdle()

        feature.execute(TestCommand.Increment)

        advanceUntilIdle()

        feature.execute(TestCommand.Decrement)

        advanceUntilIdle()

        feature.execute(TestCommand.IncrementByTwo)

        advanceUntilIdle()

        feature.execute(TestCommand.DecrementByTwo)

        advanceUntilIdle()

        assertEquals(listOf(0, 1, 0, 2, 0), states.map(TestState::count))

        assertEquals(
            listOf(
                TestEvent.Incremented,
                TestEvent.Decremented,
                TestEvent.Incremented,
                TestEvent.Incremented,
                TestEvent.Decremented,
                TestEvent.Decremented
            ), events
        )
    }
}