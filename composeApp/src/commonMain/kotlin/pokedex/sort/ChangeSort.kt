package pokedex.sort

import pokedex.PokedexRepository
import usecase.UseCase

class ChangeSort(private val repository: PokedexRepository) : UseCase<PokedexSort, PokedexSort> {
    override suspend fun execute(input: PokedexSort) = repository.changeSort(input)
}