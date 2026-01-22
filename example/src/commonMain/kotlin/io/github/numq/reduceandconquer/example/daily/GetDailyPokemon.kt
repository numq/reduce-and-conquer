package io.github.numq.reduceandconquer.example.daily

import io.github.numq.reduceandconquer.example.pokedex.PokedexRepository
import io.github.numq.reduceandconquer.example.pokemon.Pokemon
import io.github.numq.reduceandconquer.example.usecase.UseCase

class GetDailyPokemon(private val repository: PokedexRepository) : UseCase<Unit, Pokemon?> {
    override suspend fun execute(input: Unit) = runCatching {
        repository.pokedex.value.dailyPokemon
    }
}