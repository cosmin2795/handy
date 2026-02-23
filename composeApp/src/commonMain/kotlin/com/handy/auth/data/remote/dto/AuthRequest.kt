package com.handy.auth.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GoogleAuthRequest(val idToken: String)

@Serializable
data class FacebookAuthRequest(val accessToken: String)
