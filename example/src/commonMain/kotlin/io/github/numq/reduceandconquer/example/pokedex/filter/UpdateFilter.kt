package io.github.numq.reduceandconquer.example.pokedex.filter

import io.github.numq.reduceandconquer.example.pokedex.PokedexRepository
import io.github.numq.reduceandconquer.example.usecase.UseCase

class UpdateFilter(private val repository: PokedexRepository) : UseCase<UpdateFilter.Input, Unit> {
    data class Input(val filter: PokedexFilter)

    override suspend fun execute(input: Input) = repository.updateFilter(filter = input.filter)
}