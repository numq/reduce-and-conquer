package pokedex.presentation

import feature.Reducer
import feature.transition
import pokedex.sort.ChangeSort

class SortReducer(
    private val pokemonsReducer: PokemonsReducer,
    private val changeSort: ChangeSort,
) : Reducer<PokedexCommand.Sort, PokedexState, PokedexEvent> {
    override suspend fun reduce(state: PokedexState, command: PokedexCommand.Sort) = when (command) {
        is PokedexCommand.Sort.ToggleSortMode -> transition(
            state.copy(interactionMode = PokedexInteractionMode.SORT.takeIf { mode ->
                state.interactionMode != mode
            } ?: PokedexInteractionMode.NONE)
        )

        is PokedexCommand.Sort.SortPokemons -> changeSort.execute(command.sort).fold(onSuccess = {
            pokemonsReducer.reduce(
                state.copy(sort = command.sort),
                PokedexCommand.Pokemons.GetPokemons(
                    skip = 0,
                    limit = PokedexConstants.DEFAULT_LIMIT
                )
            )
        }, onFailure = {
            transition(state, PokedexEvent.Error.UnableToSelectSort())
        })
    }
}