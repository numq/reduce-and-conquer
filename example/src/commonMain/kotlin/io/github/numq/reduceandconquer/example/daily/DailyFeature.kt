package io.github.numq.reduceandconquer.example.daily

import io.github.numq.reduceandconquer.example.feature.BaseFeature
import kotlinx.coroutines.CoroutineScope

internal class DailyFeature(
    initialState: DailyState, scope: CoroutineScope, reducer: DailyReducer
) : BaseFeature<DailyState, DailyCommand, DailyEvent>(initialState = initialState, scope = scope, reducer = reducer)