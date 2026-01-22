package io.github.numq.reduceandconquer.example

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import io.github.numq.reduceandconquer.example.application.Application
import io.github.numq.reduceandconquer.example.di.appModule
import org.koin.core.context.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin { modules(appModule) }

    CanvasBasedWindow(canvasElementId = "ComposeTarget") { Application() }
}