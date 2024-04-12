package daily

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.todayIn
import pokemon.Pokemon
import pokemon.PokemonRepository
import usecase.UseCase
import kotlin.random.Random

class GetDailyPokemon(
    private val repository: PokemonRepository,
) : UseCase<Unit, Pokemon> {
    override suspend fun execute(input: Unit): Result<Pokemon> {
        return repository.getPokemons().map { pokemons ->
            val seed = Clock.System
                .todayIn(TimeZone.currentSystemDefault())
                .atStartOfDayIn(TimeZone.currentSystemDefault())
                .toEpochMilliseconds()
            pokemons.random(Random(seed))
        }
    }
}