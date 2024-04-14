package pokedex.filter

import pokedex.PokedexRepository
import usecase.UseCase

class ResetFilter(private val repository: PokedexRepository) : UseCase<PokedexFilter.Criteria, PokedexFilter> {
    override suspend fun execute(input: PokedexFilter.Criteria) = repository.resetFilter(input)
}