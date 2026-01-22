package io.github.numq.reduceandconquer.example.pokedex.sort

import io.github.numq.reduceandconquer.example.pokedex.PokedexRepository
import io.github.numq.reduceandconquer.example.usecase.UseCase

class ChangeSort(private val repository: PokedexRepository) : UseCase<PokedexSort, Unit> {
    override suspend fun execute(input: PokedexSort) = repository.changeSort(input)
}