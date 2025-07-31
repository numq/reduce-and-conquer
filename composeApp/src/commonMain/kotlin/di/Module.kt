package di

import daily.DailyFeature
import daily.DailyReducer
import daily.GetDailyPokemon
import daily.GetMaxAttributeValue
import file.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    single { NavigationReducer() }
    single { NavigationFeature(coroutineScope = CoroutineScope(Dispatchers.Default), reducer = get()) }
}

private val daily = module {
    single { GetMaxAttributeValue(get()) }
    single { GetDailyPokemon(get()) }
    single { DailyReducer(get(), get()) }
    single {
        DailyFeature(
            coroutineScope = CoroutineScope(Dispatchers.Default),
            reducer = get()
        )
    } onClose { it?.close() }
}

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
            coroutineScope = CoroutineScope(Dispatchers.Default),
            reducer = get()
        )
    } onClose { it?.close() }
}

internal val appModule = listOf(application, pokemon, navigation, daily, pokedex)