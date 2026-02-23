package com.handy.auth.domain.model

data class AuthUser(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String? = null,
)
