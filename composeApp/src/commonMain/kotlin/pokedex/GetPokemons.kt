package pokedex

import pokedex.filter.PokedexFilter
import pokemon.Pokemon
import pokemon.PokemonRepository
import usecase.UseCase

class GetPokemons(
    private val pokemonRepository: PokemonRepository,
    private val pokedexRepository: PokedexRepository,
) : UseCase<GetPokemons.Input, List<Pokemon>> {
    data class Input(val skip: Long, val limit: Long)

    override suspend fun execute(input: Input) = input.run {
        pokemonRepository.getPokemons().map { pokemons ->
            pokemons.filter { pokemon ->
                pokedexRepository.filters.values.map { filter ->
                    when (filter) {
                        is PokedexFilter.Name -> pokemon.name.lowercase().contains(filter.modified.lowercase())

                        is PokedexFilter.Type -> pokemon.types.containsAll(filter.modified)

                        is PokedexFilter.Attribute -> when (filter.kind) {
                            Pokemon.Attribute.Kind.HP -> pokemon.attributes.hp.value in filter.modified

                            Pokemon.Attribute.Kind.SPEED -> pokemon.attributes.speed.value in filter.modified

                            Pokemon.Attribute.Kind.BASIC_ATTACK -> pokemon.attributes.basicAttack.value in filter.modified

                            Pokemon.Attribute.Kind.BASIC_DEFENSE -> pokemon.attributes.basicDefense.value in filter.modified

                            Pokemon.Attribute.Kind.SPECIAL_ATTACK -> pokemon.attributes.specialAttack.value in filter.modified

                            Pokemon.Attribute.Kind.SPECIAL_DEFENSE -> pokemon.attributes.specialDefense.value in filter.modified
                        }
                    }
                }.all { it }
            }.run {
                (pokedexRepository.filters[PokedexFilter.Criteria.NAME] as? PokedexFilter.Name)?.modified?.takeIf(String::isNotBlank)
                    ?.run { sortedBy(Pokemon::name) } ?: pokedexRepository.sort.comparator.let(::sortedWith)
            }.drop(skip.toInt().coerceIn(0, pokemons.size)).take(limit.toInt().coerceIn(0, pokemons.size))
        }
    }
}