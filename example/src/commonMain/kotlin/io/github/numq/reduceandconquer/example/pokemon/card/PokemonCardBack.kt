package io.github.numq.reduceandconquer.example.pokemon.card

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.numq.reduceandconquer.example.pokemon.Pokemon
import io.github.numq.reduceandconquer.example.pokemon.chart.ChartItem
import io.github.numq.reduceandconquer.example.pokemon.chart.PokemonAttributeChart
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import reduce_and_conquer.example.generated.resources.*

@OptIn(ExperimentalResourceApi::class)
@Composable
fun PokemonCardBack(modifier: Modifier, pokemon: Pokemon, attributes: List<Pokemon.Attribute>, maxAttributeValue: Int) {
    PokemonCardCommon(modifier = modifier, pokemon = pokemon) {
        PokemonAttributeChart(
            modifier = Modifier.fillMaxSize(),
            maxAttributeValue = maxAttributeValue,
            items = attributes.map { attribute ->
                when (attribute.kind) {
                    Pokemon.Attribute.Kind.HP -> Res.string.pokemon_attribute_hp_short

                    Pokemon.Attribute.Kind.SPEED -> Res.string.pokemon_attribute_speed_short

                    Pokemon.Attribute.Kind.BASIC_ATTACK -> Res.string.pokemon_attribute_basic_attack_short

                    Pokemon.Attribute.Kind.BASIC_DEFENSE -> Res.string.pokemon_attribute_basic_defense_short

                    Pokemon.Attribute.Kind.SPECIAL_ATTACK -> Res.string.pokemon_attribute_special_attack_short

                    Pokemon.Attribute.Kind.SPECIAL_DEFENSE -> Res.string.pokemon_attribute_special_defense_short
                } to attribute.value
            }.map { (name, value) ->
                ChartItem(name = stringResource(name), value = value)
            },
            colors = pokemon.types.map(Pokemon.Type::color),
        )
    }
}