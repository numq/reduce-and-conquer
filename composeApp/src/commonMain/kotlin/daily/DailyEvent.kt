package daily

import feature.Event
import kotlinx.datetime.Clock

internal sealed interface DailyEvent : Event {
    sealed class Error(val message: String) : DailyEvent {
        data object GetMaxAttributeValue : Error("Unable to get max attribute value") {
            override val payload = null

            override val timestamp = Clock.System.now()
        }

        data object GetDailyPokemon : Error("Unable to get daily pokemon") {
            override val payload = null

            override val timestamp = Clock.System.now()
        }
    }
}