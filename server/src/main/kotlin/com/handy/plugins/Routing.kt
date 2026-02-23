package com.handy.plugins

import com.handy.auth.AuthRepository
import com.handy.auth.AuthService
import com.handy.auth.TokenService
import com.handy.auth.authRoutes
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

fun Application.configureRouting() {
    val httpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val authRepository = AuthRepository(httpClient)
    val authService = AuthService(authRepository, TokenService)

    routing {
        authRoutes(authService)
    }
}
