package notification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

@Composable
fun NotificationError(notifications: Flow<Notification.Error>) {
    val (notification, setNotification) = remember { mutableStateOf<Notification.Error?>(null) }

    val (visible, setVisible) = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        notifications.collect { notification ->
            setNotification(notification)

            setVisible(true)

            delay(notification.durationMillis)

            setVisible(false)

            delay(400)
        }
    }

    notification?.run {
        AnimatedVisibility(
            visible = visible,
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            DisposableEffect(Unit) {
                onDispose {

                    setNotification(null)
                }
            }
            Box(
                modifier = Modifier.fillMaxWidth().height(56.dp).background(MaterialTheme.colors.surface),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = message ?: "Something get wrong", textAlign = TextAlign.Center)
                    Icon(Icons.Default.ErrorOutline, null)
                }
            }
        }
    }
}