package pokedex.presentation

import daily.GetMaxAttributeValue
import feature.Reducer
import feature.transition
import pokedex.GetPokemons
import pokedex.filter.GetFilters
import pokedex.filter.PokedexFilter

class PokemonsReducer(
    private val getMaxAttributeValue: GetMaxAttributeValue,
    private val getPokemons: GetPokemons,
    private val getFilters: GetFilters,
) : Reducer<PokedexCommand.Pokemons, PokedexState, PokedexEvent> {
    override suspend fun reduce(state: PokedexState, command: PokedexCommand.Pokemons) = when (command) {
        is PokedexCommand.Pokemons.GetMaxAttributeValue -> getMaxAttributeValue.execute(Unit)
            .fold(onSuccess = { value ->
                transition(state.copy(maxAttributeValue = value))
            }, onFailure = {
                transition(state, PokedexEvent.Error.GetMaxAttributeValue())
            })

        is PokedexCommand.Pokemons.GetPokemons -> getFilters.execute(Unit).mapCatching { filters ->
            getPokemons.execute(
                GetPokemons.Input(skip = command.skip, limit = command.limit)
            ).map { pokemons -> filters to pokemons }.getOrThrow()
        }.fold(onSuccess = { (filters, pokemons) ->
            transition(
                state.copy(
                    pokemons = pokemons,
                    isFiltered = filters.filterNot { filter ->
                        filter.criteria == PokedexFilter.Criteria.NAME
                    }.any(PokedexFilter::isModified)
                ),
                PokedexEvent.ResetScroll()
            )
        }, onFailure = {
            transition(state, PokedexEvent.Error.GetPokemons())
        })

        is PokedexCommand.Pokemons.LoadMorePokemons -> getPokemons.execute(
            GetPokemons.Input(skip = state.pokemons.size.toLong(), limit = PokedexConstants.DEFAULT_LIMIT)
        ).fold(onSuccess = { pokemons ->
            transition(state.copy(pokemons = state.pokemons.plus(pokemons)))
        }, onFailure = {
            transition(state, PokedexEvent.Error.LoadMore())
        })

        is PokedexCommand.Pokemons.ResetScroll -> transition(state, PokedexEvent.ResetScroll())
    }
}