package com.handy.di

import com.handy.feature.auth.data.AuthRepositoryImpl
import com.handy.feature.auth.data.remote.AuthApi
import com.handy.feature.auth.domain.repository.AuthRepository
import com.handy.feature.auth.ui.AuthViewModel
import com.handy.feature.home.ui.HomeViewModel
import com.handy.network.createHttpClient
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformModule: Module

val networkModule = module {
    single { createHttpClient() }
    single { AuthApi(get()) }
}

val authModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { HomeViewModel(get()) }
}

val appModules = listOf(networkModule, authModule, platformModule)
