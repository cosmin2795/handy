package com.handy.auth.domain.model

sealed class AuthResult {
    data class Success(val user: AuthUser, val token: String) : AuthResult()
    data class Error(val message: String, val cause: Throwable? = null) : AuthResult()
    data object Cancelled : AuthResult()
}
