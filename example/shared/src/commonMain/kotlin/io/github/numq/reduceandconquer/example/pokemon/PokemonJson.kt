package io.github.numq.reduceandconquer.example.pokemon

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonJson(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: Name,
    @SerialName("type") val types: List<String>,
    @SerialName("base") val attributes: Attributes,
) {
    @Serializable
    data class Name(
        @SerialName("english") val english: String,
    )

    @Serializable
    data class Attributes(
        @SerialName("HP") val hp: Int,
        @SerialName("Attack") val basicAttack: Int,
        @SerialName("Defense") val basicDefense: Int,
        @SerialName("Sp. Attack") val specialAttack: Int,
        @SerialName("Sp. Defense") val specialDefense: Int,
        @SerialName("Speed") val speed: Int,
    )
}