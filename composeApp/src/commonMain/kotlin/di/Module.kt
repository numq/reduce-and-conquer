package di

import daily.DailyFeature
import daily.GetDailyPokemon
import daily.GetMaxAttributeValue
import file.FileProvider
import kotlinx.serialization.json.Json
import navigation.NavigationFeature
import org.koin.dsl.bind
import org.koin.dsl.module
import pokedex.GetPokemons
import pokedex.PokedexRepository
import pokedex.filter.*
import pokedex.presentation.PokedexFeature
import pokedex.sort.ChangeSort
import pokemon.PokemonRepository

private val application = module {
    single { FileProvider.Implementation() } bind FileProvider::class
    single { Json { ignoreUnknownKeys = true } }
}

private val pokemon = module {
    single { PokemonRepository.Implementation(get(), get()) } bind PokemonRepository::class
}

private val navigation = module {
    single { NavigationFeature() }
}

private val daily = module {
    factory { GetMaxAttributeValue(get()) }
    factory { GetDailyPokemon(get()) }
    single { DailyFeature(get(), get()) }
}

private val pokedex = module {
    single { PokedexRepository.Implementation() } bind PokedexRepository::class
    factory { GetPokemons(get(), get()) }
    factory { InitializeFilters(get(), get()) }
    factory { GetFilters(get()) }
    factory { SelectFilter(get()) }
    factory { UpdateFilter(get()) }
    factory { ResetFilters(get()) }
    factory { ChangeSort(get()) }
    single { PokedexFeature(get(), get(), get(), get(), get(), get(), get(), get()) }
}

internal val appModule = listOf(application, pokemon, navigation, daily, pokedex)