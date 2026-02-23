package com.handy.feature.auth.domain.repository

import com.handy.feature.auth.domain.model.AuthResult
import com.handy.feature.auth.domain.model.AuthUser

interface AuthRepository {
    suspend fun signInWithGoogle(): AuthResult
    suspend fun signInWithFacebook(): AuthResult
    suspend fun signOut()
    fun currentUser(): AuthUser?
}
