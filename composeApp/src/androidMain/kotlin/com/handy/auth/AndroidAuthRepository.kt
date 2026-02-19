package com.handy.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.handy.network.ApiClient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual class AuthRepository(
    private val apiClient: ApiClient,
) {
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

    actual suspend fun signInWithGoogle(): AuthResult {
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
        googleSignInClient.signOut()
        LoginManager.getInstance().logOut()
        storedUser = null
    }

    actual fun currentUser(): AuthUser? = storedUser

    companion object {
        // Replace with your actual Web Client ID from Google Cloud Console
        private const val GOOGLE_CLIENT_ID =
            "YOUR_WEB_CLIENT_ID.apps.googleusercontent.com"
    }
}

/**
 * Simple holder for the current Activity reference. Set this in MainActivity.
 * In production consider using a lifecycle-aware approach.
 */
object ActivityProvider {
    var currentActivity: Activity? = null
}

/**
 * Holds the application context, set from [HandyApplication].
 */
object AndroidContextProvider {
    lateinit var applicationContext: Context
}

/**
 * Minimal shim to wire Google Sign-In's activity-result callback.
 * Replace with the proper ActivityResultContracts approach in your activity.
 */
object ActivityResultRegistry {
    fun register(activity: Activity, callback: (Intent?) -> Unit): ActivityResultLauncher {
        return ActivityResultLauncher(activity, callback)
    }
}

class ActivityResultLauncher(
    private val activity: Activity,
    val callback: (Intent?) -> Unit,
) {
    private val requestCode = 1001

    fun launch(intent: Intent) {
        activity.startActivityForResult(intent, requestCode)
    }
}
