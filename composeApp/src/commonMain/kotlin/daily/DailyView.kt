package daily

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import notification.NotificationError
import notification.queue.rememberNotificationQueue
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import pokemon.card.PokemonCard
import reduce_and_conquer.composeapp.generated.resources.Res
import reduce_and_conquer.composeapp.generated.resources.daily_pokemon_of_the_day

@OptIn(ExperimentalResourceApi::class)
@Composable
fun DailyView(feature: DailyFeature) {
    val coroutineScope = rememberCoroutineScope()

    val notificationQueue = rememberNotificationQueue()

    val state by feature.state.collectAsState()

    LaunchedEffect(feature) {
        feature.events.filterIsInstance(DailyEvent.Error::class).collect { error ->
            notificationQueue.push(message = error.message, label = Icons.Default.ErrorOutline)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                stringResource(Res.string.daily_pokemon_of_the_day),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Box(modifier = Modifier.weight(1f).zIndex(-1f), contentAlignment = Alignment.Center) {
                androidx.compose.animation.AnimatedVisibility(
                    state.maxAttributeValue == null || state.card == null, enter = fadeIn(), exit = fadeOut()
                ) {
                    CircularProgressIndicator()
                }
                androidx.compose.animation.AnimatedVisibility(
                    state.maxAttributeValue != null && state.card != null, enter = fadeIn(), exit = fadeOut()
                ) {
                    state.maxAttributeValue?.let { maxAttributeValue ->
                        state.card?.let { card ->
                            PokemonCard(
                                modifier = Modifier.aspectRatio(.75f).padding(8.dp),
                                card = card,
                                maxAttributeValue = maxAttributeValue,
                                flip = {
                                    coroutineScope.launch {
                                        feature.execute(DailyCommand.FlipCard)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
        NotificationError(notificationQueue = notificationQueue)
    }
}