package daily

import feature.Feature

class DailyFeature(reducer: DailyReducer) : Feature<DailyCommand, DailyState, DailyEvent>(
    initialState = DailyState(),
    reducer = reducer
)