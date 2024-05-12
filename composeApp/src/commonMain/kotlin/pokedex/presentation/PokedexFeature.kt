package pokedex.presentation

import feature.Feature
import kotlinx.coroutines.launch

class PokedexFeature(reducer: PokedexReducer) : Feature<PokedexCommand, PokedexState, PokedexEvent>(
    initialState = PokedexState(),
    reducer = reducer
) {
    init {
        coroutineScope.launch {
            if (execute(PokedexCommand.Cards.GetMaxAttributeValue)) {
                if (execute(PokedexCommand.Filter.InitializeFilters)) {
                    execute(PokedexCommand.Sort.SortPokemons(state.value.sort))
                }
            }
        }
    }
}