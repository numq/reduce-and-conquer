package io.github.numq.reduceandconquer.example.daily

import io.github.numq.reduceandconquer.example.pokedex.PokedexService
import io.github.numq.reduceandconquer.example.usecase.UseCase

class GetMaxAttributeValue(
    private val service: PokedexService,
) : UseCase<Unit, Int> {
    override suspend fun execute(input: Unit) = runCatching {
        val attributeRanges = service.pokedex.value.attributeRanges

        check(attributeRanges.isNotEmpty()) { "The attribute ranges cannot be empty" }

        attributeRanges.values.maxBy(IntRange::last).last
    }
}