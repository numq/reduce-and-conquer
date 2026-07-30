package io.github.numq.reduceandconquer.example.notification.queue

import androidx.compose.ui.graphics.vector.ImageVector
import io.github.numq.reduceandconquer.example.notification.NotificationItem
import kotlinx.coroutines.channels.Channel

class DefaultQueue : NotificationQueue {
    override val notifications = Channel<NotificationItem>(Channel.BUFFERED)

    override fun push(message: String, label: ImageVector?) {
        notifications.trySend(NotificationItem(message = message, label = label))
    }

    override fun close() {
        notifications.close()
    }
}