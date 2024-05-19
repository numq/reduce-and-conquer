package pokedex.presentation

import card.FlippableCard
import daily.GetMaxAttributeValue
import feature.Reducer
import pokedex.GetPokemons

class CardsReducer(
    private val getMaxAttributeValue: GetMaxAttributeValue,
    private val getPokemons: GetPokemons,
) : Reducer<PokedexCommand.Cards, PokedexState, PokedexEvent> {
    override suspend fun reduce(state: PokedexState, command: PokedexCommand.Cards) = when (command) {
        is PokedexCommand.Cards.GetMaxAttributeValue -> getMaxAttributeValue.execute(Unit).fold(onSuccess = { value ->
            transition(state.copy(maxAttributeValue = value))
        }, onFailure = {
            transition(state, PokedexEvent.Error.GetMaxAttributeValue())
        })

        is PokedexCommand.Cards.GetCards -> getPokemons.execute(
            GetPokemons.Input(skip = command.skip, limit = command.limit)
        ).map { pokemons -> pokemons.map(::FlippableCard) }.fold(onSuccess = { cards ->
            transition(
                state.copy(
                    cards = cards.map { card -> state.cards.find { it.item.id == card.item.id } ?: card }
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