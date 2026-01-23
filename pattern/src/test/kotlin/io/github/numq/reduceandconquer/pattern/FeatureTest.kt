package io.github.numq.reduceandconquer.pattern

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
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

    private lateinit var feature: Feature<TestState, TestCommand, TestEvent>

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        feature = Feature(initialState = TestState(0), scope = testScope, reducer = TestReducer())
    }

    @AfterTest
    fun tearDown() {
        feature.close()
        Dispatchers.resetMain()
    }

    @Test
    fun `synchronous increment updates state correctly`() = runTest {
        feature.execute(TestCommand.Increment)
        advanceUntilIdle()
        assertEquals(1, feature.state.value.count)
    }

    @Test
    fun `asynchronous action updates state after delay`() = runTest {
        feature.execute(TestCommand.AsyncIncrement)

        assertEquals(0, feature.state.value.count)

        advanceTimeBy(101)

        assertEquals(1, feature.state.value.count)
    }

    @Test
    fun `error handling through command chain emits error event`() = runTest {
        val events = mutableListOf<TestEvent>()
        feature.events.onEach { events.add(it) }.launchIn(testScope)

        feature.execute(TestCommand.AsyncFailure)
        advanceUntilIdle()

        assertEquals(1, events.size)
        val errorEvent = events.first() as TestEvent.ErrorOccurred
        assertEquals("Test Error", errorEvent.message)
    }

    @Test
    fun `stream effect delivers multiple commands and updates state sequentially`() = runTest {
        val externalFlow = flow {
            emit(TestCommand.Increment)
            emit(TestCommand.Increment)
            emit(TestCommand.Increment)
        }

        feature.execute(TestCommand.StartStream(externalFlow))
        advanceUntilIdle()

        assertEquals(3, feature.state.value.count)
    }
}