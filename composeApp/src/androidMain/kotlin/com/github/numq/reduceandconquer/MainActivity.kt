package com.github.numq.reduceandconquer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import application.Application
import di.appModule
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

@Preview
@Composable
fun ApplicationAndroidPreview() {
    Application()
}