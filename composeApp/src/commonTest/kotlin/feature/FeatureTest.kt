package feature

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FeatureTest {
    @Test
    fun shouldReduceState() = runTest {
        // State
        val initialState = 0

        // Messages
        val addNumberMessage = "add number"
        val subtractNumberMessage = "subtract number"

        // Effects
        val zeroNumberEffect = object : Effect<Long> {
            override val key = Clock.System.now().toEpochMilliseconds()
        }
        val negativeNumberEffect = object : Effect<Long> {
            override val key = Clock.System.now().toEpochMilliseconds()
        }

        // Feature that allows you to add or subtract positive numbers
        val feature = object : Feature<Int, Pair<String, Int>, Effect<Long>>(initialState, backgroundScope) {
            private fun isValid(number: Int): Boolean {
                when {
                    number == 0 -> performEffect(zeroNumberEffect)

                    number < 0 -> performEffect(negativeNumberEffect)

                    else -> return true
                }
                return false
            }

            override suspend fun reduce(state: Int, message: Pair<String, Int>) = when (message.first) {
                addNumberMessage -> if (isValid(message.second)) state + message.second else state

                subtractNumberMessage -> if (isValid(message.second)) state - message.second else state

                else -> state
            }
        }

        val states = mutableListOf<Int>()
        feature.state.onEach { state ->
            states.add(state)
        }.launchIn(backgroundScope)

        val effects = mutableListOf<Effect<Long>>()
        feature.effects.onEach { effect ->
            effects.add(effect)
        }.launchIn(backgroundScope)

        assertEquals(initialState, feature.state.value)

        delay(100L)

        val messages = listOf(
            addNumberMessage to 10,
            subtractNumberMessage to 5,
            addNumberMessage to 0,
            subtractNumberMessage to -1
        )

        messages.forEach { message ->
            assertTrue(feature.dispatchMessage(message))
            delay(100L)
        }

        assertEquals(listOf(0, 10, 5), states)
        assertEquals(listOf(zeroNumberEffect, negativeNumberEffect), effects)
    }
}