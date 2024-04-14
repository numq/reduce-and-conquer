package pokedex

import pokedex.filter.PokedexFilter
import pokedex.sort.PokedexSort

interface PokedexRepository {
    val filters: Map<PokedexFilter.Criteria, PokedexFilter>
    val sort: PokedexSort
    fun addFilter(filter: PokedexFilter): Result<PokedexFilter>
    fun selectFilter(criteria: PokedexFilter.Criteria): Result<PokedexFilter>
    fun updateFilter(filter: PokedexFilter): Result<PokedexFilter>
    fun resetFilter(criteria: PokedexFilter.Criteria): Result<PokedexFilter>
    fun resetFilters(): Result<Unit>
    fun changeSort(sort: PokedexSort): Result<PokedexSort>

    class Implementation : PokedexRepository {
        override var sort: PokedexSort = PokedexSort(criteria = PokedexSort.Criteria.NAME, isAscending = true)
        override val filters = mutableMapOf<PokedexFilter.Criteria, PokedexFilter>()

        override fun addFilter(filter: PokedexFilter) = runCatching {
            filters.put(filter.criteria, filter) ?: throw Exception("Unable to add filter")
        }

        override fun selectFilter(criteria: PokedexFilter.Criteria) = runCatching {
            filters.getValue(criteria)
        }

        override fun updateFilter(filter: PokedexFilter) = runCatching {
            if (filters.containsKey(filter.criteria)) {
                filters[filter.criteria] = filter
                filters.getValue(filter.criteria)
            } else {
                throw Exception("Unable to update a non-existent filter")
            }
        }

        override fun resetFilter(criteria: PokedexFilter.Criteria) = runCatching {
            val filter = filters.getValue(criteria)
            filters[criteria] = filter.reset()
            filters.getValue(criteria)
        }

        override fun resetFilters() = runCatching {
            filters.forEach { (key, value) ->
                filters[key] = value.reset()
            }
        }

        override fun changeSort(sort: PokedexSort) = runCatching {
            this.sort = sort
            this.sort
        }
    }
}