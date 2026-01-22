package io.github.numq.reduceandconquer.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.github.numq.reduceandconquer.example.application.Application
import io.github.numq.reduceandconquer.example.di.appModule
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startKoin { modules(appModule) }

        setContent {
            Application()
        }
    }
}