package com.handy.di

import com.handy.feature.auth.data.AuthRepositoryImpl
import com.handy.feature.auth.data.remote.AuthApi
import com.handy.feature.auth.domain.repository.AuthRepository
import com.handy.feature.auth.ui.AuthViewModel
import com.handy.feature.home.ui.HomeViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.compose.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformModule: Module

val networkModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(Logging) {
                level = LogLevel.INFO
            }
        }
    }
    single { AuthApi(get()) }
}

val authModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { HomeViewModel(get()) }
}

val appModules = listOf(networkModule, authModule, platformModule)
