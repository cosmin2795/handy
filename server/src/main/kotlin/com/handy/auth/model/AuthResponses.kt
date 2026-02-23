package com.handy.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserDto,
)

@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val photoUrl: String? = null,
)
