package io.github.numq.reduceandconquer.example.pokedex.sort

import io.github.numq.reduceandconquer.example.pokedex.PokedexService
import io.github.numq.reduceandconquer.example.usecase.UseCase

class ChangeSort(private val service: PokedexService) : UseCase<PokedexSort, Unit> {
    override suspend fun execute(input: PokedexSort) = service.changeSort(input)
}