package io.github.numq.reduceandconquer.example.image

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap

actual object ImageLoader {
    actual fun loadBitmap(bytes: ByteArray) = runCatching {
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
    }.getOrNull()
}