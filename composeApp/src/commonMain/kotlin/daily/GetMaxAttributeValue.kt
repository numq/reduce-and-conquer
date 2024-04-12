package daily

import pokemon.PokemonRepository
import usecase.UseCase

class GetMaxAttributeValue(
    private val repository: PokemonRepository,
) : UseCase<Unit, Int> {
    override suspend fun execute(input: Unit) = repository.getAttributeRanges().mapCatching { attributeRanges ->
        attributeRanges.values.maxBy(IntRange::last).last
    }
}