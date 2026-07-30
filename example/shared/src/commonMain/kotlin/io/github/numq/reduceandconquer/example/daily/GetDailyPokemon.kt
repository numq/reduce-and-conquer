package io.github.numq.reduceandconquer.example.daily

import io.github.numq.reduceandconquer.example.pokedex.PokedexService
import io.github.numq.reduceandconquer.example.pokemon.Pokemon
import io.github.numq.reduceandconquer.example.usecase.UseCase

class GetDailyPokemon(private val service: PokedexService) : UseCase<Unit, Pokemon?> {
    override suspend fun execute(input: Unit) = runCatching {
        service.pokedex.value.dailyPokemon
    }
}