package daily

import feature.Feature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DailyFeature(
    coroutineScope: CoroutineScope, reducer: DailyReducer
) : Feature<DailyCommand, DailyState, DailyEvent>(
    initialState = DailyState(), coroutineScope = coroutineScope, reducer = reducer
) {
    init {
        coroutineScope.launch {
            if (execute(DailyCommand.GetMaxAttributeValue)) {
                execute(DailyCommand.GetDailyPokemon)
            }
        }
    }
}