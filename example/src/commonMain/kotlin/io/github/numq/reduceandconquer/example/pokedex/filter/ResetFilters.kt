package io.github.numq.reduceandconquer.example.pokedex.filter

import io.github.numq.reduceandconquer.example.pokedex.PokedexRepository
import io.github.numq.reduceandconquer.example.usecase.UseCase

class ResetFilters(private val repository: PokedexRepository) : UseCase<Unit, Unit> {
    override suspend fun execute(input: Unit) = repository.resetFilters()
}