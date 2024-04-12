package notification

sealed class Notification private constructor(open val durationMillis: Long) {
    data class Error(override val durationMillis: Long, val message: String?) : Notification(durationMillis)
}