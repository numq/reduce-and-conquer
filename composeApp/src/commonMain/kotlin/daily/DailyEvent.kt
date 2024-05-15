package daily

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import event.Event

sealed interface DailyEvent : Event<Uuid> {
    sealed class Error(val message: String, override val key: Uuid = uuid4()) : DailyEvent {
        data class GetMaxAttributeValue(override val key: Uuid = uuid4()) : Error("Unable to get max attribute value")
        data class GetDailyPokemon(override val key: Uuid = uuid4()) : Error("Unable to get daily pokemon")
    }
}