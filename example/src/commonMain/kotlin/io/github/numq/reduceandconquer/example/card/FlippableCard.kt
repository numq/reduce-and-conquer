package io.github.numq.reduceandconquer.example.card

data class FlippableCard<out Item>(val item: Item, val side: Side = Side.FRONT) {
    enum class Side(val angle: Float) {
        FRONT(0f), BACK(180f)
    }

    fun flip() = when (side) {
        Side.FRONT -> copy(side = Side.BACK)

        Side.BACK -> copy(side = Side.FRONT)
    }
}