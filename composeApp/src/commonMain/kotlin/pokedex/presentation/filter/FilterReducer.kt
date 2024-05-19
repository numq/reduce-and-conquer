package pokedex.presentation.filter

import feature.Reducer
import feature.mergeEvents
import pokedex.filter.*
import pokedex.presentation.*

class FilterReducer(
    private val cardsReducer: CardsReducer,
    private val initializeFilters: InitializeFilters,
    private val getFilters: GetFilters,
    private val selectFilter: SelectFilter,
    private val updateFilter: UpdateFilter,
    private val resetFilter: ResetFilter,
    private val resetFilters: ResetFilters,
) : Reducer<PokedexCommand.Filter, PokedexState, PokedexEvent> {
    override suspend fun reduce(state: PokedexState, command: PokedexCommand.Filter) = when (command) {
        is PokedexCommand.Filter.InitializeFilters -> initializeFilters.execute(Unit).mapCatching {
            getFilters.execute(Unit).getOrThrow()
        }.fold(onSuccess = { filters ->
            transition(state.copy(filters = filters))
        }, onFailure = {
            transition(state, PokedexEvent.Error.UnableToInitializeFilters())
        })

        is PokedexCommand.Filter.ToggleFilterMode -> transition(
            state.copy(interactionMode = PokedexInteractionMode.FILTER.takeIf { mode ->
                state.interactionMode != mode
            } ?: PokedexInteractionMode.NONE)
        )

        is PokedexCommand.Filter.SelectFilter -> selectFilter.execute(command.criteria).fold(onSuccess = { filter ->
            transition(state.copy(selectedFilter = filter))
        }, onFailure = {
            transition(state, PokedexEvent.Error.UnableToSelectFilter())
        })

        is PokedexCommand.Filter.UpdateFilter -> updateFilter.execute(command.filter).mapCatching { updatedFilter ->
            state.filters.map { filter ->
                if (filter.criteria == updatedFilter.criteria) updatedFilter else filter
            }.let { filters ->
                cardsReducer.reduce(
                    state.copy(
                        filters = filters,
                        isFiltered = filters.filterNot { filter ->
                            filter.criteria == PokedexFilter.Criteria.NAME
                        }.any(PokedexFilter::isModified),
                        selectedFilter = if (updatedFilter is PokedexFilter.Name) updatedFilter.takeIf(
                            PokedexFilter::isModified
                        ) else updatedFilter
                    ),
                    PokedexCommand.Cards.GetCards(
                        skip = 0,
                        limit = PokedexConstants.DEFAULT_LIMIT
                    )
                )
            }
        }.fold(onSuccess = { (state, events) ->
            cardsReducer.reduce(state, PokedexCommand.Cards.ResetScroll).mergeEvents(events)
        }, onFailure = {
            transition(state, PokedexEvent.Error.UnableToUpdateFilter())
        })

        is PokedexCommand.Filter.ResetFilter -> if (state.isFiltered) {
            resetFilter.execute(command.criteria).map { updatedFilter ->
                val filters = state.filters.map { filter ->
                    if (updatedFilter.criteria == filter.criteria) updatedFilter else filter
                }
                cardsReducer.reduce(
                    state.copy(
                        isFiltered = filters.filterNot { filter ->
                            filter.criteria == PokedexFilter.Criteria.NAME
                        }.any(PokedexFilter::isModified),
                        filters = filters,
                        selectedFilter = updatedFilter
                    ),
                    PokedexCommand.Cards.GetCards(skip = 0, limit = PokedexConstants.DEFAULT_LIMIT)
                )
            }.fold(onSuccess = { (state, events) ->
                cardsReducer.reduce(state, PokedexCommand.Cards.ResetScroll).mergeEvents(events)
            }, onFailure = {
                transition(state, PokedexEvent.Error.UnableToResetFilter())
            })
        } else transition(state)

        is PokedexCommand.Filter.CloseFilter -> transition(state.copy(selectedFilter = null))

        is PokedexCommand.Filter.ResetFilters -> if (state.isFiltered) {
            resetFilters.execute(Unit).mapCatching {
                getFilters.execute(Unit).map { filters ->
                    cardsReducer.reduce(
                        state.copy(
                            isFiltered = filters.filterNot { filter ->
                                filter.criteria == PokedexFilter.Criteria.NAME
                            }.any(PokedexFilter::isModified),
                            filters = filters,
                            selectedFilter = filters.find { filter -> filter.criteria == state.selectedFilter?.criteria }
                        ),
                        PokedexCommand.Cards.GetCards(skip = 0, limit = PokedexConstants.DEFAULT_LIMIT)
                    )
                }.getOrThrow()
            }.fold(onSuccess = { (state, events) ->
                cardsReducer.reduce(state, PokedexCommand.Cards.ResetScroll).mergeEvents(events)
            }, onFailure = {
                transition(state, PokedexEvent.Error.UnableToResetFilters())
            })
        } else transition(state)
    }
}