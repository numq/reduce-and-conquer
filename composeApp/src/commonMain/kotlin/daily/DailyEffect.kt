package daily

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import feature.Effect

sealed interface DailyEffect : Effect<Uuid> {
    data class Error(val message: String?, override val key: Uuid = uuid4()) : DailyEffect
}