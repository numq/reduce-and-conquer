package feature

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.*
import kotlin.test.*

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
        Dispatchers.resetMain()
    }

    @Test
    fun updateStateAndEmitEvent() = runTest {
        val states = buildList {
            feature.state.onEach { state ->
                add(state)
            }.launchIn(testScope)
        }

        val events = buildList {
            feature.events.onEach { event ->
                add(event)
            }.launchIn(testScope)
        }

        advanceUntilIdle()

        assertTrue(feature.execute(TestCommand.Increment))

        advanceUntilIdle()

        assertTrue(feature.execute(TestCommand.Decrement))

        advanceUntilIdle()

        assertTrue(feature.execute(TestCommand.IncrementByTwo))

        advanceUntilIdle()

        assertTrue(feature.execute(TestCommand.DecrementByTwo))

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