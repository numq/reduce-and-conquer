package feature

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class FeatureTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var feature: Feature<TestCommand, TestState, TestEvent>

    private val events = mutableListOf<TestEvent>()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        feature = object : Feature<TestCommand, TestState, TestEvent>(TestState(0), TestReducer()) {

        }

        feature.events.onEach(events::add).launchIn(CoroutineScope(testDispatcher))
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()

        events.clear()
    }

    @Test
    fun updateStateAndEmitEvent() = runTest {
        assertEquals(0, feature.state.value.count)

        feature.execute(TestCommand.Increment)

        assertEquals(1, feature.state.value.count)

        assertEquals(TestEvent.Incremented, events.last())

        feature.execute(TestCommand.Decrement)

        assertEquals(0, feature.state.value.count)

        assertEquals(TestEvent.Decremented, events.last())
    }

    @Test
    fun returnTrueOnExecutionSuccess() = runTest {
        assertTrue(feature.execute(TestCommand.Increment))
    }
}