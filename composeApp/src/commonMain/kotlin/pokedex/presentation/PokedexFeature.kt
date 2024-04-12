package pokedex.presentation

import daily.GetMaxAttributeValue
import feature.Feature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import pokedex.GetPokemons
import pokedex.filter.*
import pokedex.sort.ChangeSort

class PokedexFeature(
    private val getMaxAttributeValue: GetMaxAttributeValue,
    private val initializeFilters: InitializeFilters,
    private val getPokemons: GetPokemons,
    private val getFilters: GetFilters,
    private val selectFilter: SelectFilter,
    private val updateFilter: UpdateFilter,
    private val resetFilters: ResetFilters,
    private val changeSort: ChangeSort,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob()),
) : Feature<PokedexState, PokedexMessage, PokedexEffect>(
    initialState = PokedexState(), coroutineScope = coroutineScope
) {
    object Constants {
        const val LIMIT = 18L
    }

    private suspend fun reducePokemons(state: PokedexState, message: PokedexMessage.Pokemons) = when (message) {
        is PokedexMessage.Pokemons.GetMaxAttributeValue -> getMaxAttributeValue.execute(Unit).map { value ->
            state.copy(maxAttributeValue = value)
        }.onFailure {
            performEffect(PokedexEffect.Error.GetMaxAttributeValue())
        }

        is PokedexMessage.Pokemons.GetPokemons -> getFilters.execute(Unit).mapCatching { filters ->
            getPokemons.execute(
                GetPokemons.Input(skip = message.skip, limit = message.limit)
            ).map { pokemons ->
                state.copy(
                    pokemons = pokemons,
                    isFiltered = filters.filterNot { filter ->
                        filter.criteria == PokedexFilter.Criteria.NAME
                    }.any(PokedexFilter::isModified)
                )
            }.onSuccess {
                performEffect(PokedexEffect.ResetScroll())
            }.onFailure {
                performEffect(PokedexEffect.Error.GetPokemons())
            }.getOrThrow()
        }

        is PokedexMessage.Pokemons.LoadMorePokemons -> getFilters.execute(Unit).mapCatching { filters ->
            getPokemons.execute(
                GetPokemons.Input(skip = state.pokemons.size.toLong(), limit = Constants.LIMIT)
            ).map { pokemons ->
                state.copy(pokemons = state.pokemons.plus(pokemons))
            }.onFailure {
                performEffect(PokedexEffect.Error.LoadMore())
            }.getOrThrow()
        }
    }

    private suspend fun reduceFilter(state: PokedexState, message: PokedexMessage.Filter) = when (message) {
        is PokedexMessage.Filter.InitializeFilters -> initializeFilters.execute(Unit).mapCatching {
            getFilters.execute(Unit).map { filters ->
                state.copy(filters = filters)
            }.getOrThrow()
        }.onFailure {
            performEffect(PokedexEffect.Error.UnableToInitializeFilters())
        }

        is PokedexMessage.Filter.ToggleFilterMode -> Result.success(
            state.copy(interactionMode = PokedexInteractionMode.FILTER.takeIf { mode ->
                state.interactionMode != mode
            } ?: PokedexInteractionMode.NONE)
        )

        is PokedexMessage.Filter.SelectFilter -> selectFilter.execute(message.criteria).map { filter ->
            state.copy(selectedFilter = filter)
        }.onFailure {
            performEffect(PokedexEffect.Error.UnableToSelectFilter())
        }

        is PokedexMessage.Filter.UpdateFilter -> updateFilter.execute(message.filter).map { updatedFilter ->
            reduce(
                state.copy(
                    filters = state.filters.map { filter ->
                        if (filter.criteria == updatedFilter.criteria) updatedFilter else filter
                    },
                    selectedFilter = if (updatedFilter is PokedexFilter.Name) updatedFilter.takeIf(PokedexFilter::isModified) else updatedFilter
                ),
                PokedexMessage.Pokemons.GetPokemons(skip = 0, limit = Constants.LIMIT)
            )
        }.onFailure {
            performEffect(PokedexEffect.Error.UnableToUpdateFilter())
        }

        is PokedexMessage.Filter.CloseFilter -> Result.success(
            state.copy(selectedFilter = null)
        )

        is PokedexMessage.Filter.ResetFilters -> {
            if (state.isFiltered) {
                resetFilters.execute(Unit).mapCatching {
                    getFilters.execute(Unit).map { filters ->
                        reduce(
                            state.copy(
                                filters = filters,
                                selectedFilter = filters.find { filter -> filter.criteria == state.selectedFilter?.criteria }
                            ),
                            PokedexMessage.Pokemons.GetPokemons(skip = 0, limit = Constants.LIMIT)
                        )
                    }.getOrThrow()
                }.onFailure {
                    performEffect(PokedexEffect.Error.UnableToResetFilters())
                }
            } else {
                Result.success(state)
            }
        }
    }

    private suspend fun reduceSort(state: PokedexState, message: PokedexMessage.Sort) = when (message) {
        is PokedexMessage.Sort.ToggleSortMode -> Result.success(
            state.copy(interactionMode = PokedexInteractionMode.SORT.takeIf { mode ->
                state.interactionMode != mode
            } ?: PokedexInteractionMode.NONE)
        )

        is PokedexMessage.Sort.SortPokemons -> changeSort.execute(message.sort).map {
            reduce(
                state.copy(sort = message.sort),
                PokedexMessage.Pokemons.GetPokemons(skip = 0, limit = Constants.LIMIT)
            )
        }.onFailure {
            performEffect(PokedexEffect.Error.UnableToSelectSort())
        }
    }

    override suspend fun reduce(state: PokedexState, message: PokedexMessage): PokedexState = when (message) {
        is PokedexMessage.Pokemons -> reducePokemons(state, message)

        is PokedexMessage.Filter -> reduceFilter(state, message)

        is PokedexMessage.Sort -> reduceSort(state, message)
    }.getOrDefault(state)

    init {
        if (dispatchMessage(PokedexMessage.Pokemons.GetMaxAttributeValue)) {
            if (dispatchMessage(PokedexMessage.Filter.InitializeFilters)) {
                dispatchMessage(PokedexMessage.Sort.SortPokemons(state.value.sort))
            }
        }
    }
}