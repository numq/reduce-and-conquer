package daily

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import event.Event

sealed interface DailyEvent : Event<Uuid> {
    data class Error(val message: String?, override val key: Uuid = uuid4()) : DailyEvent
}