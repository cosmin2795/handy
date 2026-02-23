package com.handy.feature.auth.domain.repository

import com.handy.feature.auth.domain.model.AuthResult
import com.handy.feature.auth.domain.model.AuthUser

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): AuthResult
    suspend fun signInWithFacebook(accessToken: String): AuthResult
    suspend fun signOut()
    fun currentUser(): AuthUser?
}
