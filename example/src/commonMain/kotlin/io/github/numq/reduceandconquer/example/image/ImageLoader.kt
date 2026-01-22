package io.github.numq.reduceandconquer.example.image

import androidx.compose.ui.graphics.ImageBitmap

expect object ImageLoader {
    fun loadBitmap(bytes: ByteArray): ImageBitmap?
}