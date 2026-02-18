package com.handy

import androidx.compose.ui.window.ComposeUIViewController
import com.handy.di.appModules
import org.koin.compose.KoinContext
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        startKoin {
            modules(appModules)
        }
    },
) {
    KoinContext {
        App()
    }
}
