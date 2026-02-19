package com.handy.auth

object JwtConfig {
    val secret: String = System.getenv("JWT_SECRET") ?: "dev-secret-change-in-production"
    val issuer: String = System.getenv("JWT_ISSUER") ?: "https://handy.com"
    val audience: String = System.getenv("JWT_AUDIENCE") ?: "handy-app"
    val realm: String = "Handy App"
    val expirationMs: Long = 30L * 24 * 60 * 60 * 1000 // 30 days
}
