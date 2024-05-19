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

    private val events = mutableListOf<TestEvent>()

    private lateinit var feature: Feature<TestCommand, TestState, TestEvent>

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        feature = object : Feature<TestCommand, TestState, TestEvent>(TestState(0), TestReducer()) {}

        feature.events.onEach { event ->
            events.add(event)
        }.launchIn(testScope)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()

        events.clear()
    }

    @Test
    fun updateStateAndEmitEvent() = runTest {
        assertEquals(0, feature.state.value.count)

        assertTrue(feature.execute(TestCommand.Increment))
        assertEquals(1, feature.state.value.count)

        assertTrue(feature.execute(TestCommand.Decrement))
        assertEquals(0, feature.state.value.count)

        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(listOf(TestEvent.Incremented, TestEvent.Decremented), events)
    }
}