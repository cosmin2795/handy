package com.handy.feature.auth.ui

import androidx.compose.runtime.Composable

@Composable
actual fun rememberGoogleAuthLauncher(
    onToken: (idToken: String) -> Unit,
    onError: (message: String) -> Unit,
    onCancel: () -> Unit,
): () -> Unit {
    // TODO: Wire GIDSignIn.sharedInstance.signIn(withPresenting:) via Objective-C interop
    return {}
}
