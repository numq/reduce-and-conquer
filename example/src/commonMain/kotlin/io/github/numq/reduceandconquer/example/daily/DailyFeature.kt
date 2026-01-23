package io.github.numq.reduceandconquer.example.daily

import io.github.numq.reduceandconquer.example.feature.Feature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

internal class DailyFeature(reducer: DailyReducer) : Feature<DailyState, DailyCommand, DailyEvent> by Feature(
    initialState = DailyState(), scope = CoroutineScope(Dispatchers.Default + SupervisorJob()), reducer = reducer
)