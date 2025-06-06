package pokedex.presentation

import feature.Feature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PokedexFeature(
    coroutineScope: CoroutineScope,
    reducer: PokedexReducer
) : Feature<PokedexCommand, PokedexState, PokedexEvent>(
    initialState = PokedexState(),
    coroutineScope = coroutineScope,
    reducer = reducer
) {
    init {
        coroutineScope.launch {
            execute(PokedexCommand.Cards.GetMaxAttributeValue)

            execute(PokedexCommand.Filter.InitializeFilters)

            execute(PokedexCommand.Sort.SortPokemons(state.value.sort))
        }
    }
}