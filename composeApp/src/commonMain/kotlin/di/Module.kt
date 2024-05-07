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
import pokedex.GetPokemons
import pokedex.PokedexRepository
import pokedex.filter.*
import pokedex.presentation.*
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
    single { NavigationFeature(reducer = get()) }
}

private val daily = module {
    factory { GetMaxAttributeValue(get()) }
    factory { GetDailyPokemon(get()) }
    single { DailyReducer(get(), get()) }
    single { DailyFeature(reducer = get()) }
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
    single { PokedexReducer(get(), get(), get()) }
    single { PokemonsReducer(get(), get(), get()) }
    single { FilterReducer(get(), get(), get(), get(), get(), get(), get()) }
    single { SortReducer(get(), get()) }
    single { PokedexFeature(get()) }
}

internal val appModule = listOf(application, pokemon, navigation, daily, pokedex)