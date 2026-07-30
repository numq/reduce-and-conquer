package io.github.numq.reduceandconquer.example

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.numq.reduceandconquer.example.application.Application
import io.github.numq.reduceandconquer.example.di.initKoin

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "reduce-and-conquer",
    ) {
        initKoin()

        Application()
    }
}