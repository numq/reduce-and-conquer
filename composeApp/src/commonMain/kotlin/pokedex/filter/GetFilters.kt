package pokedex.filter

import pokedex.PokedexRepository
import usecase.UseCase

class GetFilters(private val repository: PokedexRepository) : UseCase<Unit, List<PokedexFilter>> {
    override suspend fun execute(input: Unit) = Result.success(repository.filters.values.toList())
}