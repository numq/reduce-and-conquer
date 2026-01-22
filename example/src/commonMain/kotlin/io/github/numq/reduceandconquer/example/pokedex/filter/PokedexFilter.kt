package io.github.numq.reduceandconquer.example.pokedex.filter

import io.github.numq.reduceandconquer.example.pokemon.Pokemon

sealed class PokedexFilter(open val criteria: Criteria) {
    enum class Criteria {
        NAME, TYPE, HP, SPEED, BASIC_ATTACK, BASIC_DEFENSE, SPECIAL_ATTACK, SPECIAL_DEFENSE
    }

    abstract fun isModified(): Boolean

    abstract fun reset(): PokedexFilter

    abstract fun matches(pokemon: Pokemon): Boolean

    data class Name(
        val default: String,
        val modified: String = default,
    ) : PokedexFilter(criteria = Criteria.NAME) {
        override fun isModified() = default != modified

        override fun reset() = copy(modified = default)

        override fun matches(pokemon: Pokemon): Boolean {
            if (modified.isBlank()) return true

            return pokemon.name.contains(modified, ignoreCase = true)
        }
    }

    data class Type(
        val default: Set<Pokemon.Type>,
        val modified: Set<Pokemon.Type> = default,
    ) : PokedexFilter(criteria = Criteria.TYPE) {
        override fun isModified() = default != modified

        override fun reset() = copy(modified = default)

        override fun matches(pokemon: Pokemon): Boolean {
            if (modified.isEmpty()) return true

            return pokemon.types.containsAll(modified)
        }
    }

    data class Attribute(
        override val criteria: Criteria,
        val kind: Pokemon.Attribute.Kind,
        val default: IntRange,
        val modified: IntRange = default,
    ) : PokedexFilter(criteria) {
        override fun isModified() = default != modified

        override fun reset() = copy(modified = default)

        override fun matches(pokemon: Pokemon): Boolean {
            val value = when (kind) {
                Pokemon.Attribute.Kind.HP -> pokemon.attributes.hp.value

                Pokemon.Attribute.Kind.SPEED -> pokemon.attributes.speed.value

                Pokemon.Attribute.Kind.BASIC_ATTACK -> pokemon.attributes.basicAttack.value

                Pokemon.Attribute.Kind.BASIC_DEFENSE -> pokemon.attributes.basicDefense.value

                Pokemon.Attribute.Kind.SPECIAL_ATTACK -> pokemon.attributes.specialAttack.value

                Pokemon.Attribute.Kind.SPECIAL_DEFENSE -> pokemon.attributes.specialDefense.value
            }

            return value in modified
        }
    }
}