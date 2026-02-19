package com.handy.auth

data class AuthUser(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String? = null,
)

sealed class AuthResult {
    data class Success(val user: AuthUser, val token: String) : AuthResult()
    data class Error(val message: String, val cause: Throwable? = null) : AuthResult()
    data object Cancelled : AuthResult()
}
