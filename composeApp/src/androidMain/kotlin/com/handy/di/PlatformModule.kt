package com.handy.di

import com.handy.auth.AuthRepository
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { AuthRepository(get()) }
}
