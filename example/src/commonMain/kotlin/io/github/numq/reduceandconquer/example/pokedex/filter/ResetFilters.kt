package io.github.numq.reduceandconquer.example.pokedex.filter

import io.github.numq.reduceandconquer.example.pokedex.PokedexService
import io.github.numq.reduceandconquer.example.usecase.UseCase

class ResetFilters(private val service: PokedexService) : UseCase<Unit, Unit> {
    override suspend fun execute(input: Unit) = service.resetFilters()
}