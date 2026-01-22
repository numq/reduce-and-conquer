package io.github.numq.reduceandconquer.example.pokedex.presentation

import io.github.numq.reduceandconquer.example.feature.BaseFeature
import kotlinx.coroutines.CoroutineScope

internal class PokedexFeature(
    initialState: PokedexState, scope: CoroutineScope, reducer: PokedexReducer
) : BaseFeature<PokedexState, PokedexCommand, PokedexEvent>(
    initialState = initialState, scope = scope, reducer = reducer,
)