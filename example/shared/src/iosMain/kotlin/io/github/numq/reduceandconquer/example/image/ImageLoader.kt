package io.github.numq.reduceandconquer.example.image

import androidx.compose.ui.graphics.asComposeImageBitmap
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Image

actual object ImageLoader {
    actual fun loadBitmap(bytes: ByteArray) = runCatching {
        Bitmap.makeFromImage(Image.makeFromEncoded(bytes)).asComposeImageBitmap()
    }.getOrNull()
}