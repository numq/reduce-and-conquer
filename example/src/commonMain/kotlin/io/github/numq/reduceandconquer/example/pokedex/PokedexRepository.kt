package io.github.numq.reduceandconquer.example.pokedex

import io.github.numq.reduceandconquer.example.pokedex.filter.PokedexFilter
import io.github.numq.reduceandconquer.example.pokedex.sort.PokedexSort
import io.github.numq.reduceandconquer.example.pokemon.Pokemon
import io.github.numq.reduceandconquer.example.pokemon.PokemonService
import io.github.numq.reduceandconquer.example.pokemon.toPokemon
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.todayIn
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

private typealias FilterCommand = (Map<PokedexFilter.Criteria, PokedexFilter>) -> Map<PokedexFilter.Criteria, PokedexFilter>

interface PokedexRepository {
    val pokedex: StateFlow<Pokedex>

    suspend fun updateFilter(filter: PokedexFilter): Result<Unit>

    suspend fun resetFilter(criteria: PokedexFilter.Criteria): Result<Unit>

    suspend fun resetFilters(): Result<Unit>

    suspend fun changeSort(sort: PokedexSort): Result<Unit>

    @OptIn(ExperimentalStdlibApi::class)
    class Implementation(
        service: PokemonService, dispatcher: CoroutineContext = Dispatchers.Default
    ) : PokedexRepository, AutoCloseable {
        private val scope = CoroutineScope(dispatcher + SupervisorJob())

        private val _pokemons = service.pokemons.map { pokemonJsons ->
            pokemonJsons.map { pokemonJson ->
                val imagePath = service.getPokemonImagePath(id = pokemonJson.id).getOrNull()

                pokemonJson.toPokemon().copy(imagePath = imagePath)
            }
        }

        private val _dailyPokemon = _pokemons.map(::calculateDailyPokemon).distinctUntilChanged()

        private val _attributeRanges =
            _pokemons.filter(List<Pokemon>::isNotEmpty).distinctUntilChanged().map(::calculateRanges)

        private val _filterCommands = MutableSharedFlow<FilterCommand>()

        @OptIn(ExperimentalCoroutinesApi::class)
        private val _filters = _attributeRanges.flatMapLatest { ranges ->
            val initialMap = createInitialFilters(ranges)

            _filterCommands.scan(initialMap) { currentMap, transform ->
                transform(currentMap)
            }
        }.stateIn(scope, SharingStarted.Eagerly, emptyMap())

        private val _sort = MutableStateFlow(Pokedex.defaultSort)

        private val _filteredPokemons = combine(_pokemons, _filters, _sort) { pokemons, filters, sort ->
            pokemons.asSequence().filter { pokemon ->
                filters.values.all { pokedexFilter -> pokedexFilter.matches(pokemon) }
            }.sortedWith(sort.comparator).toList()
        }

        private val _maxAttributeValue = _attributeRanges.map { attributeRanges ->
            attributeRanges.values.maxOfOrNull(IntRange::last) ?: 0
        }

        override val pokedex = combine(
            _filteredPokemons, _dailyPokemon, _filters, _sort, combine(
                _attributeRanges, _maxAttributeValue
            ) { attributeRanges, maxAttributeValue ->
                attributeRanges to maxAttributeValue
            }) { items, daily, filters, sort, (attributeRanges, maxAttributeValue) ->
            Pokedex(
                pokemons = items,
                dailyPokemon = daily,
                attributeRanges = attributeRanges,
                filters = filters,
                sort = sort,
                maxAttributeValue = maxAttributeValue
            )
        }.stateIn(scope, SharingStarted.Eagerly, Pokedex())

        private fun createInitialFilters(ranges: Map<Pokemon.Attribute.Kind, IntRange>): Map<PokedexFilter.Criteria, PokedexFilter> {
            val base = mutableMapOf(
                PokedexFilter.Criteria.NAME to PokedexFilter.Name(default = ""),
                PokedexFilter.Criteria.TYPE to PokedexFilter.Type(default = emptySet())
            )

            ranges.forEach { (kind, range) ->
                val criteria = when (kind) {
                    Pokemon.Attribute.Kind.HP -> PokedexFilter.Criteria.HP

                    Pokemon.Attribute.Kind.SPEED -> PokedexFilter.Criteria.SPEED

                    Pokemon.Attribute.Kind.BASIC_ATTACK -> PokedexFilter.Criteria.BASIC_ATTACK

                    Pokemon.Attribute.Kind.BASIC_DEFENSE -> PokedexFilter.Criteria.BASIC_DEFENSE

                    Pokemon.Attribute.Kind.SPECIAL_ATTACK -> PokedexFilter.Criteria.SPECIAL_ATTACK

                    Pokemon.Attribute.Kind.SPECIAL_DEFENSE -> PokedexFilter.Criteria.SPECIAL_DEFENSE
                }

                base[criteria] = PokedexFilter.Attribute(criteria, kind, range)
            }
            return base
        }

        private fun calculateDailyPokemon(list: List<Pokemon>): Pokemon? {
            if (list.isEmpty()) return null

            val timeZone = TimeZone.currentSystemDefault()

            val seed = Clock.System.todayIn(timeZone).atStartOfDayIn(timeZone).toEpochMilliseconds()

            return list.random(Random(seed))
        }

        private fun calculateRanges(list: List<Pokemon>): Map<Pokemon.Attribute.Kind, IntRange> {
            val initialStats = Pokemon.Attribute.Kind.entries.associateWith {
                Int.MAX_VALUE to Int.MIN_VALUE
            }

            val resultStats = list.fold(initialStats) { acc, pokemon ->
                val attrs = pokemon.attributes

                acc.mapValues { (kind, currentMinMax) ->
                    val value = when (kind) {
                        Pokemon.Attribute.Kind.HP -> attrs.hp.value

                        Pokemon.Attribute.Kind.SPEED -> attrs.speed.value

                        Pokemon.Attribute.Kind.BASIC_ATTACK -> attrs.basicAttack.value

                        Pokemon.Attribute.Kind.BASIC_DEFENSE -> attrs.basicDefense.value

                        Pokemon.Attribute.Kind.SPECIAL_ATTACK -> attrs.specialAttack.value

                        Pokemon.Attribute.Kind.SPECIAL_DEFENSE -> attrs.specialDefense.value
                    }

                    val (min, max) = currentMinMax

                    minOf(min, value) to maxOf(max, value)
                }
            }

            return resultStats.mapValues { (_, minMax) ->
                val (min, max) = minMax

                min..max
            }
        }

        override suspend fun updateFilter(filter: PokedexFilter) = runCatching {
            _filterCommands.emit { current ->
                current + (filter.criteria to filter)
            }
        }

        override suspend fun resetFilter(criteria: PokedexFilter.Criteria) = runCatching {
            _filterCommands.emit { current ->
                when (val filter = current[criteria]) {
                    null -> current

                    else -> current + (criteria to filter.reset())
                }
            }
        }

        override suspend fun resetFilters() = runCatching {
            _filterCommands.emit { current ->
                current.mapValues { (_, value) -> value.reset() }
            }
        }

        override suspend fun changeSort(sort: PokedexSort) = runCatching {
            _sort.value = sort
        }

        override fun close() {
            scope.cancel()
        }
    }
}