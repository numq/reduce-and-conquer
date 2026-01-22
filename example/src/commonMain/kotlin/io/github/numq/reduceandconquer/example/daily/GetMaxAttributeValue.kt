package io.github.numq.reduceandconquer.example.daily

import io.github.numq.reduceandconquer.example.pokedex.PokedexRepository
import io.github.numq.reduceandconquer.example.usecase.UseCase

class GetMaxAttributeValue(
    private val repository: PokedexRepository,
) : UseCase<Unit, Int> {
    override suspend fun execute(input: Unit) = runCatching {
        val attributeRanges = repository.pokedex.value.attributeRanges

        check(attributeRanges.isNotEmpty()) { "The attribute ranges cannot be empty" }

        attributeRanges.values.maxBy(IntRange::last).last
    }
}