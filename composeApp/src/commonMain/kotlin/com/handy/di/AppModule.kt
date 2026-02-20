package com.handy.di

import com.handy.auth.AuthRepository
import com.handy.auth.AuthViewModel
import com.handy.network.ApiClient
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Platform-specific Koin module. Each platform (Android/iOS) provides its own
 * implementation of platform-dependent dependencies.
 */
expect val platformModule: Module

val networkModule = module {
    single { ApiClient() }
}

val authModule = module {
    viewModel { AuthViewModel(get()) }
}

val appModules = listOf(networkModule, authModule, platformModule)
