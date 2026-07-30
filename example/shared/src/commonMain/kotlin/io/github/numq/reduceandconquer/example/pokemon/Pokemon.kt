package io.github.numq.reduceandconquer.example.pokemon

data class Pokemon(
    val id: Int,
    val name: String,
    val types: Set<Type>,
    val attributes: Attributes,
    val imagePath: String? = null,
) {
    enum class Type(val color: Long) {
        NORMAL(0xFFA8A878), FIGHTING(0xFFC03028), FLYING(0xFFA890F0), POISON(0xFFA040A0), GROUND(0xFFE0C068), ROCK(
            0xFFB8A038
        ),
        BUG(0xFFA8B820), GHOST(0xFF705898), STEEL(0xFFB8B8D0), FIRE(0xFFF08030), WATER(0xFF6890F0), GRASS(0xFF78C850), ELECTRIC(
            0xFFF8D030
        ),
        PSYCHIC(0xFFF85888), ICE(0xFF98D8D8), DRAGON(0xFF7038F8), DARK(0xFF705848), FAIRY(0xFFEE99AC)
    }

    data class Attribute(val kind: Kind, val value: Int) {
        enum class Kind {
            HP, SPEED, BASIC_ATTACK, BASIC_DEFENSE, SPECIAL_ATTACK, SPECIAL_DEFENSE
        }
    }

    data class Attributes(
        val hp: Attribute,
        val speed: Attribute,
        val basicAttack: Attribute,
        val basicDefense: Attribute,
        val specialAttack: Attribute,
        val specialDefense: Attribute,
    )
}