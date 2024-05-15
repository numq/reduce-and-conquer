package notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import notification.queue.NotificationQueue

@Composable
fun NotificationError(notificationQueue: NotificationQueue) {
    Notification(notificationQueue = notificationQueue) { item ->
        Box(
            modifier = Modifier.fillMaxWidth().height(56.dp).background(MaterialTheme.colors.surface),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = item.message, textAlign = TextAlign.Center)
                Icon(Icons.Default.ErrorOutline, null)
            }
        }
    }
}