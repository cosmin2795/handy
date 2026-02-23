package com.handy.feature.auth.data

import android.content.Context
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.handy.feature.auth.ActivityProvider
import com.handy.feature.auth.ActivityResultRegistry
import com.handy.feature.auth.AndroidContextProvider
import com.handy.feature.auth.data.remote.AuthApi
import com.handy.feature.auth.domain.model.AuthResult
import com.handy.feature.auth.domain.model.AuthUser
import com.handy.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual class AuthRepositoryImpl actual constructor(
    private val authApi: AuthApi,
) : AuthRepository {

    private val context: Context get() = AndroidContextProvider.applicationContext

    private var storedUser: AuthUser? = null

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(GOOGLE_CLIENT_ID)
            .requestEmail()
            .requestProfile()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    private val facebookCallbackManager: CallbackManager = CallbackManager.Factory.create()

    actual override suspend fun signInWithGoogle(): AuthResult {
        return try {
            val account = suspendCancellableCoroutine { continuation ->
                val activity = ActivityProvider.currentActivity
                    ?: return@suspendCancellableCoroutine continuation.resume(null)

                val launcher = ActivityResultRegistry.register(activity) { result ->
                    if (result != null) {
                        GoogleSignIn.getSignedInAccountFromIntent(result)
                            .addOnSuccessListener { continuation.resume(it) }
                            .addOnFailureListener { continuation.resume(null) }
                    } else {
                        continuation.resume(null)
                    }
                }
                launcher.launch(googleSignInClient.signInIntent)
            }

            if (account == null) return AuthResult.Cancelled

            val idToken = account.idToken
                ?: return AuthResult.Error("Google ID token not available")

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
            val loginResult = suspendCancellableCoroutine { continuation ->
                val activity = ActivityProvider.currentActivity
                    ?: return@suspendCancellableCoroutine continuation.resume(null)

                LoginManager.getInstance().registerCallback(
                    facebookCallbackManager,
                    object : FacebookCallback<LoginResult> {
                        override fun onSuccess(result: LoginResult) = continuation.resume(result)
                        override fun onCancel() = continuation.resume(null)
                        override fun onError(error: FacebookException) = continuation.resume(null)
                    },
                )
                LoginManager.getInstance()
                    .logInWithReadPermissions(activity, listOf("public_profile", "email"))
            }

            if (loginResult == null) return AuthResult.Cancelled

            val accessToken = loginResult.accessToken.token
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
        googleSignInClient.signOut()
        LoginManager.getInstance().logOut()
        storedUser = null
    }

    actual override fun currentUser(): AuthUser? = storedUser

    companion object {
        // Replace with your actual Web Client ID from Google Cloud Console
        private const val GOOGLE_CLIENT_ID = "YOUR_WEB_CLIENT_ID.apps.googleusercontent.com"
    }
}
