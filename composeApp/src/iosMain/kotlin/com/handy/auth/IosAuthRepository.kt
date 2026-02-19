package com.handy.auth

import com.handy.network.ApiClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual class AuthRepository(
    private val apiClient: ApiClient,
) {
    private var storedUser: AuthUser? = null

    actual suspend fun signInWithGoogle(): AuthResult {
        return try {
            // On iOS, use the GoogleSignIn SDK via Objective-C interop.
            // The pattern below is pseudocode — wire this to GIDSignIn.sharedInstance.
            val idToken = suspendCancellableCoroutine<String?> { continuation ->
                // GIDSignIn.sharedInstance.signIn(withPresenting: rootViewController) { result, error in
                //     continuation.resume(result?.user.idToken?.tokenString)
                // }
                // For now, resume with null to indicate no native integration yet.
                continuation.resume(null)
            } ?: return AuthResult.Cancelled

            val response = apiClient.authenticateWithGoogle(idToken)
            val user = AuthUser(
                id = response.user.id,
                name = response.user.name,
                email = response.user.email,
                photoUrl = response.user.photoUrl,
            )
            storedUser = user
            AuthResult.Success(user = user, token = response.token)
        } catch (e: Exception) {
            AuthResult.Error(message = e.message ?: "Google sign-in failed", cause = e)
        }
    }

    actual suspend fun signInWithFacebook(): AuthResult {
        return try {
            // On iOS, use the Facebook iOS SDK via Objective-C interop.
            // Wire this to FBSDKLoginManager.
            val accessToken = suspendCancellableCoroutine<String?> { continuation ->
                // let manager = LoginManager()
                // manager.logIn(permissions: ["public_profile", "email"], from: viewController) { result, error in
                //     continuation.resume(result?.token?.tokenString)
                // }
                // For now, resume with null to indicate no native integration yet.
                continuation.resume(null)
            } ?: return AuthResult.Cancelled

            val response = apiClient.authenticateWithFacebook(accessToken)
            val user = AuthUser(
                id = response.user.id,
                name = response.user.name,
                email = response.user.email,
                photoUrl = response.user.photoUrl,
            )
            storedUser = user
            AuthResult.Success(user = user, token = response.token)
        } catch (e: Exception) {
            AuthResult.Error(message = e.message ?: "Facebook sign-in failed", cause = e)
        }
    }

    actual suspend fun signOut() {
        // GIDSignIn.sharedInstance.signOut()
        // LoginManager().logOut()
        storedUser = null
    }

    actual fun currentUser(): AuthUser? = storedUser
}
