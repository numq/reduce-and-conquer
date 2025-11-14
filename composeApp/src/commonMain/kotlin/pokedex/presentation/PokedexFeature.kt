package pokedex.presentation

import feature.Feature
import kotlinx.coroutines.*

internal class PokedexFeature(
    private val feature: Feature<PokedexCommand, PokedexState>
) : Feature<PokedexCommand, PokedexState> by feature {
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    init {
        coroutineScope.launch {
            execute(PokedexCommand.Cards.GetMaxAttributeValue)

            execute(PokedexCommand.Filter.InitializeFilters)

            execute(PokedexCommand.Sort.SortPokemons(state.value.sort))
        }
    }

    override val invokeOnClose: (suspend () -> Unit)? get() = { coroutineScope.cancel() }
}