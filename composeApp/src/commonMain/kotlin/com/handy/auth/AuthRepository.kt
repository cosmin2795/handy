package com.handy.auth

/**
 * Platform-specific authentication repository.
 *
 * Each platform (Android/iOS) provides an `actual` implementation that:
 * 1. Launches the native Google/Facebook sign-in flow.
 * 2. Receives the provider token (ID token or access token).
 * 3. Sends the token to the Ktor backend (/auth/google or /auth/facebook).
 * 4. Returns an [AuthResult] with the app-issued JWT on success.
 */
expect class AuthRepository {
    suspend fun signInWithGoogle(): AuthResult
    suspend fun signInWithFacebook(): AuthResult
    suspend fun signOut()
    fun currentUser(): AuthUser?
}
