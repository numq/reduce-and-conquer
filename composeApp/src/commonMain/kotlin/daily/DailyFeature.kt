package daily

import feature.Feature
import kotlinx.coroutines.launch

class DailyFeature(reducer: DailyReducer) : Feature<DailyCommand, DailyState, DailyEvent>(
    initialState = DailyState(),
    reducer = reducer
) {
    init {
        coroutineScope.launch {
            if (execute(DailyCommand.GetMaxAttributeValue)) {
                execute(DailyCommand.GetDailyPokemon)
            }
        }
    }
}