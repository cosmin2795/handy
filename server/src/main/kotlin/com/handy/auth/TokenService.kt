package com.handy.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

object TokenService {
    fun generateToken(userId: String, email: String, name: String): String =
        JWT.create()
            .withAudience(JwtConfig.audience)
            .withIssuer(JwtConfig.issuer)
            .withClaim("userId", userId)
            .withClaim("email", email)
            .withClaim("name", name)
            .withExpiresAt(Date(System.currentTimeMillis() + JwtConfig.expirationMs))
            .sign(Algorithm.HMAC256(JwtConfig.secret))
}
