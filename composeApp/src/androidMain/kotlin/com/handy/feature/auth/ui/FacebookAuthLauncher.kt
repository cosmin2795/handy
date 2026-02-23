package com.handy.feature.auth.ui

import androidx.activity.result.ActivityResultRegistryOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

@Composable
actual fun rememberFacebookAuthLauncher(
    onToken: (accessToken: String) -> Unit,
    onError: (message: String) -> Unit,
    onCancel: () -> Unit,
): () -> Unit {
    val callbackManager = remember { CallbackManager.Factory.create() }
    val context = LocalContext.current

    DisposableEffect(Unit) {
        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) = onToken(result.accessToken.token)
                override fun onCancel() = onCancel()
                override fun onError(error: FacebookException) =
                    onError(error.message ?: "Facebook sign-in failed")
            },
        )
        onDispose { LoginManager.getInstance().unregisterCallback(callbackManager) }
    }

    return {
        // ActivityResultRegistryOwner is implemented by ComponentActivity, available via context
        val registryOwner = context as? ActivityResultRegistryOwner
        if (registryOwner != null) {
            LoginManager.getInstance().logIn(
                registryOwner,
                callbackManager,
                listOf("public_profile", "email"),
            )
        } else {
            onError("Facebook sign-in requires an Activity context")
        }
    }
}
