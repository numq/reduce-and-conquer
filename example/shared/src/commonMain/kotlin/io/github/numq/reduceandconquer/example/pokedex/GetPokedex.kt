package io.github.numq.reduceandconquer.example.pokedex

import io.github.numq.reduceandconquer.example.usecase.UseCase
import kotlinx.coroutines.flow.StateFlow

class GetPokedex(private val service: PokedexService) : UseCase<Unit, StateFlow<Pokedex>> {
    override suspend fun execute(input: Unit) = runCatching {
        service.pokedex
    }
}