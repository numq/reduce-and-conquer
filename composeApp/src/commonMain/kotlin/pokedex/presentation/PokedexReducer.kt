package pokedex.presentation

import feature.Reducer

class PokedexReducer(
    private val cardsReducer: CardsReducer,
    private val filterReducer: FilterReducer,
    private val sortReducer: SortReducer,
) : Reducer<PokedexCommand, PokedexState, PokedexEvent> {
    override suspend fun reduce(state: PokedexState, command: PokedexCommand) = when (command) {
        is PokedexCommand.Cards -> cardsReducer.reduce(state, command)

        is PokedexCommand.Filter -> filterReducer.reduce(state, command)

        is PokedexCommand.Sort -> sortReducer.reduce(state, command)
    }
}