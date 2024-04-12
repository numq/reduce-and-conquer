package pokedex.filter

import pokedex.PokedexRepository
import pokemon.Pokemon
import pokemon.PokemonRepository
import usecase.UseCase

class InitializeFilters(
    private val pokemonRepository: PokemonRepository,
    private val pokedexRepository: PokedexRepository,
) : UseCase<Unit, Unit> {
    override suspend fun execute(input: Unit) = pokemonRepository.getAttributeRanges().mapCatching { attributeRanges ->
        listOf(
            PokedexFilter.Name(default = ""),
            PokedexFilter.Type(default = emptySet())
        ).plus(
            attributeRanges.map { (key, value) ->
                when (key) {
                    Pokemon.Attribute.Kind.HP -> PokedexFilter.Attribute(
                        criteria = PokedexFilter.Criteria.HP,
                        kind = Pokemon.Attribute.Kind.HP,
                        default = value
                    )

                    Pokemon.Attribute.Kind.SPEED -> PokedexFilter.Attribute(
                        criteria = PokedexFilter.Criteria.SPEED,
                        kind = Pokemon.Attribute.Kind.SPEED,
                        default = value
                    )

                    Pokemon.Attribute.Kind.BASIC_ATTACK -> PokedexFilter.Attribute(
                        criteria = PokedexFilter.Criteria.BASIC_ATTACK,
                        kind = Pokemon.Attribute.Kind.BASIC_ATTACK,
                        default = value
                    )

                    Pokemon.Attribute.Kind.BASIC_DEFENSE -> PokedexFilter.Attribute(
                        criteria = PokedexFilter.Criteria.BASIC_DEFENSE,
                        kind = Pokemon.Attribute.Kind.BASIC_DEFENSE,
                        default = value
                    )

                    Pokemon.Attribute.Kind.SPECIAL_ATTACK -> PokedexFilter.Attribute(
                        criteria = PokedexFilter.Criteria.SPECIAL_ATTACK,
                        kind = Pokemon.Attribute.Kind.SPECIAL_ATTACK,
                        default = value
                    )

                    Pokemon.Attribute.Kind.SPECIAL_DEFENSE -> PokedexFilter.Attribute(
                        criteria = PokedexFilter.Criteria.SPECIAL_DEFENSE,
                        kind = Pokemon.Attribute.Kind.SPECIAL_DEFENSE,
                        default = value
                    )
                }
            }
        ).forEach(pokedexRepository::addFilter)
    }
}