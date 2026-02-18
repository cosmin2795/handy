package com.handy.di

import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    // iOS-specific bindings go here.
    // AuthRepository is registered in the common authModule using the actual class.
}
