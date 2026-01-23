package io.github.numq.reduceandconquer.example.pokedex.presentation

import io.github.numq.reduceandconquer.example.feature.Feature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

internal class PokedexFeature(reducer: PokedexReducer) : Feature<PokedexState, PokedexCommand, PokedexEvent> by Feature(
    initialState = PokedexState(),
    scope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
    reducer = reducer,
)