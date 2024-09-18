package di

import daily.DailyFeature
import daily.DailyReducer
import daily.GetDailyPokemon
import daily.GetMaxAttributeValue
import file.FileProvider
import kotlinx.serialization.json.Json
import navigation.NavigationFeature
import navigation.NavigationReducer
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.onClose
import pokedex.GetPokemons
import pokedex.PokedexRepository
import pokedex.filter.*
import pokedex.presentation.CardsReducer
import pokedex.presentation.PokedexFeature
import pokedex.presentation.PokedexReducer
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
    factory { NavigationReducer() }
    single { NavigationFeature(reducer = get()) }
}

private val daily = module {
    factory { GetMaxAttributeValue(get()) }
    factory { GetDailyPokemon(get()) }
    factory { DailyReducer(get(), get()) }
    single { DailyFeature(reducer = get()) } onClose { it?.close() }
}

private val pokedex = module {
    single { PokedexRepository.Implementation() } bind PokedexRepository::class
    factory { GetPokemons(get(), get()) }
    factory { InitializeFilters(get(), get()) }
    factory { GetFilters(get()) }
    factory { SelectFilter(get()) }
    factory { UpdateFilter(get()) }
    factory { ResetFilter(get()) }
    factory { ResetFilters(get()) }
    factory { ChangeSort(get()) }
    factory { PokedexReducer(get(), get(), get()) }
    factory { CardsReducer(get(), get()) }
    factory { FilterReducer(get(), get(), get(), get(), get(), get(), get()) }
    factory { SortReducer(get(), get()) }
    single { PokedexFeature(get()) } onClose { it?.close() }
}

internal val appModule = listOf(application, pokemon, navigation, daily, pokedex)