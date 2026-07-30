package io.github.numq.reduceandconquer.example.image

import org.jetbrains.compose.resources.decodeToImageBitmap

actual object ImageLoader {
    actual fun loadBitmap(bytes: ByteArray) = runCatching {
        bytes.inputStream().readAllBytes().decodeToImageBitmap()
    }.getOrNull()
}