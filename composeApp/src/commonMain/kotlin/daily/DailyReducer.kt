package daily

import card.FlippableCard
import feature.Reducer
import feature.transition

class DailyReducer(
    private val getMaxAttributeValue: GetMaxAttributeValue,
    private val getDailyPokemon: GetDailyPokemon,
) : Reducer<DailyCommand, DailyState, DailyEvent> {
    override suspend fun reduce(state: DailyState, command: DailyCommand) = when (command) {
        is DailyCommand.GetMaxAttributeValue -> getMaxAttributeValue.execute(Unit).fold(onSuccess = { value ->
            transition(state.copy(maxAttributeValue = value))
        }, onFailure = {
            transition(state, DailyEvent.Error(it.message))
        })

        is DailyCommand.GetDailyPokemon -> getDailyPokemon.execute(Unit).fold(onSuccess = { pokemon ->
            transition(state.copy(card = FlippableCard(item = pokemon)))
        }, onFailure = {
            transition(state, DailyEvent.Error(it.message))
        })

        is DailyCommand.FlipCard -> transition(state.copy(card = state.card?.flip()))
    }
}