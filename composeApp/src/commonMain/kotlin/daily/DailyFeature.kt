package daily

import feature.Feature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay

class DailyFeature(
    private val getDailyPokemon: GetDailyPokemon,
    private val getMaxAttributeValue: GetMaxAttributeValue,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
) : Feature<DailyState, DailyMessage, DailyEffect>(
    initialState = DailyState(),
    coroutineScope = coroutineScope
) {
    override suspend fun reduce(state: DailyState, message: DailyMessage) = when (message) {
        is DailyMessage.GetMaxAttributeValue -> getMaxAttributeValue.execute(Unit).map { value ->
            state.copy(maxAttributeValue = value)
        }.onFailure {
            performEffect(DailyEffect.Error(it.message))
        }

        is DailyMessage.GetDailyPokemon -> getDailyPokemon.execute(Unit).map { pokemon ->
            state.copy(pokemon = pokemon)
        }.onSuccess {
            delay(1000L)
        }.onFailure {
            performEffect(DailyEffect.Error(it.message))
        }
    }.getOrDefault(state)

    init {
        if (dispatchMessage(DailyMessage.GetMaxAttributeValue)) {
            dispatchMessage(DailyMessage.GetDailyPokemon)
        }
    }
}