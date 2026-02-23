package com.handy.feature.auth.data

import com.handy.feature.auth.data.remote.AuthApi
import com.handy.feature.auth.domain.model.AuthResult
import com.handy.feature.auth.domain.model.AuthUser
import com.handy.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual class AuthRepositoryImpl actual constructor(
    private val authApi: AuthApi,
) : AuthRepository {

    private var storedUser: AuthUser? = null

    actual override suspend fun signInWithGoogle(): AuthResult {
        return try {
            // On iOS, use the GoogleSignIn SDK via Objective-C interop.
            // Wire this to GIDSignIn.sharedInstance.signIn(withPresenting:) below.
            val idToken = suspendCancellableCoroutine<String?> { continuation ->
                // GIDSignIn.sharedInstance.signIn(withPresenting: rootViewController) { result, error in
                //     continuation.resume(result?.user.idToken?.tokenString)
                // }
                continuation.resume(null)
            } ?: return AuthResult.Cancelled

            val response = authApi.authenticateWithGoogle(idToken)
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

    actual override suspend fun signInWithFacebook(): AuthResult {
        return try {
            // On iOS, use the Facebook iOS SDK via Objective-C interop.
            // Wire this to FBSDKLoginManager below.
            val accessToken = suspendCancellableCoroutine<String?> { continuation ->
                // let manager = LoginManager()
                // manager.logIn(permissions: ["public_profile", "email"], from: viewController) { result, error in
                //     continuation.resume(result?.token?.tokenString)
                // }
                continuation.resume(null)
            } ?: return AuthResult.Cancelled

            val response = authApi.authenticateWithFacebook(accessToken)
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

    actual override suspend fun signOut() {
        // GIDSignIn.sharedInstance.signOut()
        // LoginManager().logOut()
        storedUser = null
    }

    actual override fun currentUser(): AuthUser? = storedUser
}
