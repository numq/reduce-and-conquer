package pokedex.presentation

import card.FlippableCard
import daily.GetMaxAttributeValue
import feature.Reducer
import feature.transition
import pokedex.GetPokemons
import pokedex.filter.GetFilters
import pokedex.filter.PokedexFilter

class CardsReducer(
    private val getMaxAttributeValue: GetMaxAttributeValue,
    private val getPokemons: GetPokemons,
    private val getFilters: GetFilters,
) : Reducer<PokedexCommand.Cards, PokedexState, PokedexEvent> {
    override suspend fun reduce(state: PokedexState, command: PokedexCommand.Cards) = when (command) {
        is PokedexCommand.Cards.GetMaxAttributeValue -> getMaxAttributeValue.execute(Unit).fold(onSuccess = { value ->
            transition(state.copy(maxAttributeValue = value))
        }, onFailure = {
            transition(state, PokedexEvent.Error.GetMaxAttributeValue())
        })

        is PokedexCommand.Cards.GetCards -> getFilters.execute(Unit).mapCatching { filters ->
            getPokemons.execute(
                GetPokemons.Input(skip = command.skip, limit = command.limit)
            ).map { pokemons -> filters to pokemons.map(::FlippableCard) }.getOrThrow()
        }.fold(onSuccess = { (filters, cards) ->
            transition(
                state.copy(
                    cards = cards,
                    isFiltered = filters.filterNot { filter ->
                        filter.criteria == PokedexFilter.Criteria.NAME
                    }.any(PokedexFilter::isModified)
                ),
                PokedexEvent.ResetScroll()
            )
        }, onFailure = {
            transition(state, PokedexEvent.Error.GetPokemons())
        })

        is PokedexCommand.Cards.LoadMoreCards -> getPokemons.execute(
            GetPokemons.Input(skip = state.cards.size.toLong(), limit = PokedexConstants.DEFAULT_LIMIT)
        ).map { pokemons ->
            pokemons.map(::FlippableCard)
        }.fold(onSuccess = { cards ->
            transition(state.copy(cards = state.cards.plus(cards)))
        }, onFailure = {
            transition(state, PokedexEvent.Error.LoadMore())
        })

        is PokedexCommand.Cards.FlipCard -> transition(
            state.copy(
                cards = state.cards.map { card ->
                    if (card.item.id == command.card.item.id) card.flip() else card
                }
            )
        )

        is PokedexCommand.Cards.ResetScroll -> transition(state, PokedexEvent.ResetScroll())
    }
}