package io.github.numq.reduceandconquer.example.pokedex.filter

import io.github.numq.reduceandconquer.example.pokedex.PokedexService
import io.github.numq.reduceandconquer.example.usecase.UseCase

class UpdateFilter(private val service: PokedexService) : UseCase<UpdateFilter.Input, Unit> {
    data class Input(val filter: PokedexFilter)

    override suspend fun execute(input: Input) = service.updateFilter(filter = input.filter)
}