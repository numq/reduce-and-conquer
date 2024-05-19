package pokedex.filter

import pokemon.Pokemon

sealed class PokedexFilter private constructor(open val criteria: Criteria) {
    enum class Criteria {
        NAME, TYPE, HP, SPEED, BASIC_ATTACK, BASIC_DEFENSE, SPECIAL_ATTACK, SPECIAL_DEFENSE
    }

    abstract fun isModified(): Boolean
    abstract fun reset(): PokedexFilter

    data class Name(
        val default: String,
        val modified: String = default,
    ) : PokedexFilter(criteria = Criteria.NAME) {
        override fun isModified() = default.contentEquals(modified).not()
        override fun reset() = Name(default = default, modified = default)
    }

    data class Type(
        val default: Set<Pokemon.Type>,
        val modified: Set<Pokemon.Type> = default,
    ) : PokedexFilter(criteria = Criteria.TYPE) {
        override fun isModified() = default.toTypedArray().contentEquals(modified.toTypedArray()).not()
        override fun reset() = Type(default = default, modified = default)
    }

    data class Attribute(
        override val criteria: Criteria,
        val kind: Pokemon.Attribute.Kind,
        val default: IntRange,
        val modified: IntRange = default,
    ) : PokedexFilter(criteria) {
        override fun isModified() = default != modified
        override fun reset() = Attribute(criteria = criteria, kind = kind, default = default, modified = default)
    }
}