package io.github.numq.reduceandconquer.example.pokedex.filter

import io.github.numq.reduceandconquer.example.pokedex.PokedexService
import io.github.numq.reduceandconquer.example.usecase.UseCase

class ResetFilter(private val service: PokedexService) : UseCase<ResetFilter.Input, Unit> {
    data class Input(val criteria: PokedexFilter.Criteria)

    override suspend fun execute(input: Input) = service.resetFilter(criteria = input.criteria)
}