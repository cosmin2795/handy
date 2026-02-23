package com.handy.auth

import com.handy.auth.model.FacebookUserInfo
import com.handy.auth.model.GoogleTokenInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class AuthRepository(private val httpClient: HttpClient) {

    suspend fun verifyGoogleToken(idToken: String): GoogleTokenInfo =
        httpClient.get("https://oauth2.googleapis.com/tokeninfo") {
            parameter("id_token", idToken)
        }.body()

    suspend fun verifyFacebookToken(accessToken: String): FacebookUserInfo =
        httpClient.get("https://graph.facebook.com/me") {
            parameter("access_token", accessToken)
            parameter("fields", "id,name,email")
        }.body()
}
