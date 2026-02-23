package com.handy.auth.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleTokenInfo(
    val sub: String,
    val email: String,
    val name: String,
    @SerialName("picture") val photoUrl: String? = null,
    val aud: String,
    val iss: String,
)

@Serializable
data class FacebookUserInfo(
    val id: String,
    val name: String,
    val email: String? = null,
)
