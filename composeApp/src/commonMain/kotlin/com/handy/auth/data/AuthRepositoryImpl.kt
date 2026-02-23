package com.handy.auth.data

import com.handy.auth.data.remote.AuthApi
import com.handy.auth.domain.model.AuthResult
import com.handy.auth.domain.model.AuthUser
import com.handy.auth.domain.repository.AuthRepository

expect class AuthRepositoryImpl(authApi: AuthApi) : AuthRepository {
    override suspend fun signInWithGoogle(): AuthResult
    override suspend fun signInWithFacebook(): AuthResult
    override suspend fun signOut()
    override fun currentUser(): AuthUser?
}
