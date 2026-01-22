package io.github.numq.reduceandconquer.example.pokedex

import io.github.numq.reduceandconquer.example.usecase.UseCase
import kotlinx.coroutines.flow.StateFlow

class GetPokedex(private val repository: PokedexRepository) : UseCase<Unit, StateFlow<Pokedex>> {
    override suspend fun execute(input: Unit) = runCatching {
        repository.pokedex
    }
}