package pokedex.filter

import pokedex.PokedexRepository
import usecase.UseCase

class SelectFilter(private val repository: PokedexRepository) : UseCase<PokedexFilter.Criteria, PokedexFilter?> {
    override suspend fun execute(input: PokedexFilter.Criteria) = repository.selectFilter(input)
}