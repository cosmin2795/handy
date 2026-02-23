package com.handy.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class GoogleAuthRequest(val idToken: String)

@Serializable
data class FacebookAuthRequest(val accessToken: String)
