package com.handy.auth.data.remote

import com.handy.auth.data.remote.dto.AuthResponse
import com.handy.auth.data.remote.dto.FacebookAuthRequest
import com.handy.auth.data.remote.dto.GoogleAuthRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApi(
    private val httpClient: HttpClient,
    private val baseUrl: String = "http://10.0.2.2:8080",
) {
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
