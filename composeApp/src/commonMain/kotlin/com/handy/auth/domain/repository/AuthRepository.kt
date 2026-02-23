package com.handy.auth.domain.repository

import com.handy.auth.domain.model.AuthResult
import com.handy.auth.domain.model.AuthUser

interface AuthRepository {
    suspend fun signInWithGoogle(): AuthResult
    suspend fun signInWithFacebook(): AuthResult
    suspend fun signOut()
    fun currentUser(): AuthUser?
}
