package com.handy.di

import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    // Android-specific bindings (e.g., Context-aware dependencies) go here.
    // AuthRepository is registered in the common authModule using the actual class.
}
