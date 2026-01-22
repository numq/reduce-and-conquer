package io.github.numq.reduceandconquer.example.pokedex.filter

import io.github.numq.reduceandconquer.example.pokedex.PokedexRepository
import io.github.numq.reduceandconquer.example.usecase.UseCase

class ResetFilter(private val repository: PokedexRepository) : UseCase<ResetFilter.Input, Unit> {
    data class Input(val criteria: PokedexFilter.Criteria)

    override suspend fun execute(input: Input) = repository.resetFilter(criteria = input.criteria)
}