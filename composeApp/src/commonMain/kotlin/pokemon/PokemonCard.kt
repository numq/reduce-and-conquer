package pokemon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import image.ImageLoader
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import pokemon.chart.ChartItem
import pokemon.chart.PokemonAttributeChart
import reduce_and_conquer.composeapp.generated.resources.*

@OptIn(ExperimentalResourceApi::class)
@Composable
fun PokemonCard(modifier: Modifier, pokemon: Pokemon, maxAttributeValue: Int) {
    val attributes = remember(pokemon.attributes) {
        listOf(
            pokemon.attributes.specialAttack,
            pokemon.attributes.specialDefense,
            pokemon.attributes.speed,
            pokemon.attributes.basicDefense,
            pokemon.attributes.basicAttack,
            pokemon.attributes.hp
        )
    }

    val bitmap = remember(pokemon) { pokemon.imageBytes?.let { ImageLoader.loadBitmap(it) } }

    Card {
        Column(
            modifier = modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = pokemon.name, fontSize = 24.sp)
            bitmap?.run {
                Image(bitmap, pokemon.name, modifier = Modifier.weight(1f))
            } ?: Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.BrokenImage, pokemon.name)
            }
            Row(
                modifier = Modifier.width(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                pokemon.types.forEach { type ->
                    Card(modifier = Modifier.weight(1f), backgroundColor = Color(type.color)) {
                        Text(
                            text = type.name,
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
            PokemonAttributeChart(
                modifier = Modifier.size(300.dp).padding(8.dp),
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
}