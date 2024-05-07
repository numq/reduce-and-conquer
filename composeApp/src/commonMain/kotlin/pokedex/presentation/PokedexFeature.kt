package pokedex.presentation

import feature.Feature

class PokedexFeature(reducer: PokedexReducer) : Feature<PokedexCommand, PokedexState, PokedexEvent>(
    initialState = PokedexState(),
    reducer = reducer
)