package pokedex.filter

import pokedex.PokedexRepository
import usecase.UseCase

class UpdateFilter(private val repository: PokedexRepository) : UseCase<PokedexFilter, PokedexFilter> {
    override suspend fun execute(input: PokedexFilter) = repository.updateFilter(input)
}