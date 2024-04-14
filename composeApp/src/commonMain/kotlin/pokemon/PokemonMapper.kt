package pokemon

fun PokemonJson.toPokemon() = Pokemon(
    id = id,
    name = name.english,
    types = types.map(String::uppercase).map(Pokemon.Type::valueOf).toSet(),
    attributes = Pokemon.Attributes(
        hp = Pokemon.Attribute(
            Pokemon.Attribute.Kind.HP, attributes.hp
        ), speed = Pokemon.Attribute(
            Pokemon.Attribute.Kind.SPEED, attributes.speed
        ), basicAttack = Pokemon.Attribute(
            Pokemon.Attribute.Kind.BASIC_ATTACK, attributes.basicAttack
        ), basicDefense = Pokemon.Attribute(
            Pokemon.Attribute.Kind.BASIC_DEFENSE, attributes.basicDefense
        ), specialAttack = Pokemon.Attribute(
            Pokemon.Attribute.Kind.SPECIAL_ATTACK, attributes.specialAttack
        ), specialDefense = Pokemon.Attribute(
            Pokemon.Attribute.Kind.SPECIAL_DEFENSE, attributes.specialDefense
        )
    )
)