package io.github.numq.reduceandconquer.example.image

import androidx.compose.ui.res.loadImageBitmap

actual object ImageLoader {
    actual fun loadBitmap(bytes: ByteArray) = runCatching {
        loadImageBitmap(bytes.inputStream())
    }.getOrNull()
}