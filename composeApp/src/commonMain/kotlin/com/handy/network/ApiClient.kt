package com.handy.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class GoogleAuthRequest(val idToken: String)

@Serializable
data class FacebookAuthRequest(val accessToken: String)

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserDto,
)

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String? = null,
)

class ApiClient(private val baseUrl: String = "http://10.0.2.2:8080") {

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    suspend fun authenticateWithGoogle(idToken: String): AuthResponse =
        httpClient.post("$baseUrl/auth/google") {
            contentType(ContentType.Application.Json)
            setBody(GoogleAuthRequest(idToken))
        }.body()

    suspend fun authenticateWithFacebook(accessToken: String): AuthResponse =
        httpClient.post("$baseUrl/auth/facebook") {
            contentType(ContentType.Application.Json)
            setBody(FacebookAuthRequest(accessToken))
        }.body()
}
