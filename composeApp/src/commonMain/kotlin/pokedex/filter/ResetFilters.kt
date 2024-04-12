package pokedex.filter

import pokedex.PokedexRepository
import usecase.UseCase

class ResetFilters(private val repository: PokedexRepository) : UseCase<Unit, Unit> {
    override suspend fun execute(input: Unit) = repository.resetFilters()
}