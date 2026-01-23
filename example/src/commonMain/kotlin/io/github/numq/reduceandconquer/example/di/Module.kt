package io.github.numq.reduceandconquer.example.di

import io.github.numq.reduceandconquer.example.daily.DailyFeature
import io.github.numq.reduceandconquer.example.daily.DailyReducer
import io.github.numq.reduceandconquer.example.daily.GetDailyPokemon
import io.github.numq.reduceandconquer.example.daily.GetMaxAttributeValue
import io.github.numq.reduceandconquer.example.file.FileProvider
import io.github.numq.reduceandconquer.example.navigation.NavigationFeature
import io.github.numq.reduceandconquer.example.navigation.NavigationReducer
import io.github.numq.reduceandconquer.example.pokedex.GetPokedex
import io.github.numq.reduceandconquer.example.pokedex.PokedexRepository
import io.github.numq.reduceandconquer.example.pokedex.filter.ResetFilter
import io.github.numq.reduceandconquer.example.pokedex.filter.ResetFilters
import io.github.numq.reduceandconquer.example.pokedex.filter.UpdateFilter
import io.github.numq.reduceandconquer.example.pokedex.presentation.PokedexFeature
import io.github.numq.reduceandconquer.example.pokedex.presentation.PokedexReducer
import io.github.numq.reduceandconquer.example.pokedex.presentation.filter.FilterReducer
import io.github.numq.reduceandconquer.example.pokedex.presentation.sort.SortReducer
import io.github.numq.reduceandconquer.example.pokedex.sort.ChangeSort
import io.github.numq.reduceandconquer.example.pokemon.PokemonService
import kotlinx.serialization.json.Json
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.dsl.onClose

private val application = module {
    single { FileProvider.Implementation() } bind FileProvider::class
    single { Json { ignoreUnknownKeys = true } }
}

private val pokemon = module {
    single { PokemonService.Implementation(get(), get()) } bind PokemonService::class
}

private val navigation = module {
    single { NavigationReducer() }
    single { NavigationFeature(reducer = get()) } onClose { it?.close() }
}

private val daily = module {
    single { GetDailyPokemon(get()) }
    single { GetMaxAttributeValue(get()) }
    single { DailyReducer(get()) }
    single { DailyFeature(reducer = get()) } onClose { it?.close() }
}

private val pokedex = module {
    single { PokedexRepository.Implementation(service = get()) } bind PokedexRepository::class onClose { it?.close() }
    single { GetPokedex(get()) }
    single { ResetFilter(get()) }
    single { ResetFilters(get()) }
    single { UpdateFilter(get()) }
    single { ChangeSort(get()) }
    single { FilterReducer(get(), get(), get()) }
    single { PokedexReducer(get(), get(), get()) }
    single { SortReducer(get()) }
    single { PokedexFeature(reducer = PokedexReducer(get(), get(), get())) } onClose { it?.close() }
}

internal val appModule = listOf(application, pokemon, navigation, daily, pokedex)