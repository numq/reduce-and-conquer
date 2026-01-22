package io.github.numq.reduceandconquer.example.pokedex.sort

import io.github.numq.reduceandconquer.example.pokemon.Pokemon

data class PokedexSort(val criteria: Criteria, val isAscending: Boolean) {
    enum class Criteria {
        NAME, HP, SPEED, BASIC_ATTACK, BASIC_DEFENSE, SPECIAL_ATTACK, SPECIAL_DEFENSE
    }

    val comparator: Comparator<Pokemon> by lazy {
        val baseComparator = compareBy<Pokemon> { pokemon ->
            when (criteria) {
                Criteria.NAME -> pokemon.name

                Criteria.HP -> pokemon.attributes.hp.value

                Criteria.SPEED -> pokemon.attributes.speed.value

                Criteria.BASIC_ATTACK -> pokemon.attributes.basicAttack.value

                Criteria.BASIC_DEFENSE -> pokemon.attributes.basicDefense.value

                Criteria.SPECIAL_ATTACK -> pokemon.attributes.specialAttack.value

                Criteria.SPECIAL_DEFENSE -> pokemon.attributes.specialDefense.value
            }
        }

        if (isAscending) baseComparator else baseComparator.reversed()
    }
}