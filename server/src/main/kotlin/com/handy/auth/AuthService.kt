package com.handy.auth

import com.handy.auth.model.AuthResponse
import com.handy.auth.model.UserDto

class AuthService(
    private val authRepository: AuthRepository,
    private val tokenService: TokenService,
) {

    suspend fun loginWithGoogle(idToken: String): AuthResponse {
        val tokenInfo = authRepository.verifyGoogleToken(idToken)

        val expectedAudience = System.getenv("GOOGLE_CLIENT_ID") ?: ""
        if (expectedAudience.isNotBlank() && tokenInfo.aud != expectedAudience) {
            throw IllegalArgumentException("Token audience mismatch")
        }

        val userId = "google_${tokenInfo.sub}"
        val token = tokenService.generateToken(
            userId = userId,
            email = tokenInfo.email,
            name = tokenInfo.name,
        )

        return AuthResponse(
            token = token,
            user = UserDto(
                id = userId,
                name = tokenInfo.name,
                email = tokenInfo.email,
                photoUrl = tokenInfo.photoUrl,
            ),
        )
    }

    suspend fun loginWithFacebook(accessToken: String): AuthResponse {
        val fbUser = authRepository.verifyFacebookToken(accessToken)

        val userId = "facebook_${fbUser.id}"
        val email = fbUser.email ?: "$userId@facebook.com"
        val token = tokenService.generateToken(
            userId = userId,
            email = email,
            name = fbUser.name,
        )

        return AuthResponse(
            token = token,
            user = UserDto(
                id = userId,
                name = fbUser.name,
                email = email,
            ),
        )
    }
}
