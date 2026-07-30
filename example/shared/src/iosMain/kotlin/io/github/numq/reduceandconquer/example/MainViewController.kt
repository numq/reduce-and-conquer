package io.github.numq.reduceandconquer.example

import androidx.compose.ui.window.ComposeUIViewController
import io.github.numq.reduceandconquer.example.application.Application
import io.github.numq.reduceandconquer.example.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }) {
    Application()
}