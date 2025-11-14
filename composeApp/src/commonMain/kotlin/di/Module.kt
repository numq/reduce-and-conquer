package di

import daily.*
import feature.factory.CommandStrategy
import feature.factory.FeatureFactory
import file.FileProvider
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import navigation.NavigationFeature
import navigation.NavigationReducer
import navigation.NavigationState
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.onClose
import pokedex.GetPokemons
import pokedex.PokedexRepository
import pokedex.filter.*
import pokedex.presentation.CardsReducer
import pokedex.presentation.PokedexFeature
import pokedex.presentation.PokedexReducer
import pokedex.presentation.PokedexState
import pokedex.presentation.filter.FilterReducer
import pokedex.presentation.sort.SortReducer
import pokedex.sort.ChangeSort
import pokemon.PokemonRepository
import pokemon.PokemonService

private val application = module {
    single { FileProvider.Implementation() } bind FileProvider::class
    single { Json { ignoreUnknownKeys = true } }
}

private val pokemon = module {
    single { PokemonService.Implementation(get(), get()) } bind PokemonService::class
    single { PokemonRepository.Implementation(get()) } bind PokemonRepository::class
}

private val navigation = module {
    single { NavigationReducer() }
    single {
        NavigationFeature(
            feature = FeatureFactory().create(
                initialState = NavigationState.Daily,
                reducer = NavigationReducer(),
                strategy = CommandStrategy.Immediate
            )
        )
    }
}

@OptIn(DelicateCoroutinesApi::class)
private val daily = module {
    single { GetMaxAttributeValue(get()) }
    single { GetDailyPokemon(get()) }
    single { DailyReducer(get(), get()) }
    single {
        DailyFeature(
            feature = FeatureFactory().create(
                initialState = DailyState(),
                reducer = DailyReducer(get(), get()),
                strategy = CommandStrategy.Immediate
            )
        )
    } onClose { GlobalScope.launch { it?.close() } }
}

@OptIn(DelicateCoroutinesApi::class)
private val pokedex = module {
    single { PokedexRepository.Implementation() } bind PokedexRepository::class
    single { GetPokemons(get(), get()) }
    single { InitializeFilters(get(), get()) }
    single { GetFilters(get()) }
    single { SelectFilter(get()) }
    single { UpdateFilter(get()) }
    single { ResetFilter(get()) }
    single { ResetFilters(get()) }
    single { ChangeSort(get()) }
    single { PokedexReducer(get(), get(), get()) }
    single { CardsReducer(get(), get()) }
    single { FilterReducer(get(), get(), get(), get(), get(), get(), get()) }
    single { SortReducer(get(), get()) }
    single {
        PokedexFeature(
            feature = FeatureFactory().create(
                initialState = PokedexState(),
                reducer = PokedexReducer(get(), get(), get()),
                strategy = CommandStrategy.Immediate
            )
        )
    } onClose { GlobalScope.launch { it?.close() } }
}

internal val appModule = listOf(application, pokemon, navigation, daily, pokedex)