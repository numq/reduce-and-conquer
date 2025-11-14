package daily

import feature.Feature
import kotlinx.coroutines.*

internal class DailyFeature(
    private val feature: Feature<DailyCommand, DailyState>
) : Feature<DailyCommand, DailyState> by feature {
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    init {
        coroutineScope.launch {
            execute(DailyCommand.GetMaxAttributeValue)

            execute(DailyCommand.GetDailyPokemon)
        }
    }

    override val invokeOnClose: (suspend () -> Unit)? get() = { coroutineScope.cancel() }
}