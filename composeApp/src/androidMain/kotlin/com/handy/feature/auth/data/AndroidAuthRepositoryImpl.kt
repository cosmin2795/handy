package com.handy.feature.auth.data

import com.handy.feature.auth.data.remote.AuthApi
import com.handy.feature.auth.domain.model.AuthResult
import com.handy.feature.auth.domain.model.AuthUser
import com.handy.feature.auth.domain.repository.AuthRepository

actual class AuthRepositoryImpl actual constructor(
    private val authApi: AuthApi,
) : AuthRepository {

    private var storedUser: AuthUser? = null

    actual override suspend fun signInWithGoogle(idToken: String): AuthResult {
        return try {
            val response = authApi.authenticateWithGoogle(idToken)
            val user = AuthUser(
                id = response.user.id,
                name = response.user.name,
                email = response.user.email,
                photoUrl = response.user.photoUrl,
            )
            storedUser = user
            AuthResult.Success(user = user, token = response.token)
        } catch (e: Exception) {
            AuthResult.Error(message = e.message ?: "Google sign-in failed", cause = e)
        }
    }

    actual override suspend fun signInWithFacebook(accessToken: String): AuthResult {
        return try {
            val response = authApi.authenticateWithFacebook(accessToken)
            val user = AuthUser(
                id = response.user.id,
                name = response.user.name,
                email = response.user.email,
                photoUrl = response.user.photoUrl,
            )
            storedUser = user
            AuthResult.Success(user = user, token = response.token)
        } catch (e: Exception) {
            AuthResult.Error(message = e.message ?: "Facebook sign-in failed", cause = e)
        }
    }

    actual override suspend fun signOut() {
        storedUser = null
    }

    actual override fun currentUser(): AuthUser? = storedUser
}
