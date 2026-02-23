package com.handy.feature.auth.domain.model

data class AuthUser(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String? = null,
)
