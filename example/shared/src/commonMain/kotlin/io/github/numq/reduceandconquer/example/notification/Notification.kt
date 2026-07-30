package io.github.numq.reduceandconquer.example.notification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.github.numq.reduceandconquer.example.notification.queue.NotificationQueue
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
fun Notification(notificationQueue: NotificationQueue, content: @Composable (NotificationItem) -> Unit) {
    val (notification, setNotification) = remember { mutableStateOf<NotificationItem?>(null) }

    LaunchedEffect(notificationQueue) {
        for (item in notificationQueue.notifications) {
            setNotification(item)
            delay(2.seconds)
            setNotification(null)
            delay(300.milliseconds)
        }
    }

    AnimatedVisibility(
        visible = notification != null,
        enter = slideInVertically { it },
        exit = slideOutVertically { it },
        modifier = Modifier.clickable { setNotification(null) }) {
        notification?.let { content(it) }
    }
}