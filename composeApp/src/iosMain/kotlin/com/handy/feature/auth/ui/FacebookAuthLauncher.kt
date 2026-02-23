package com.handy.feature.auth.ui

import androidx.compose.runtime.Composable

@Composable
actual fun rememberFacebookAuthLauncher(
    onToken: (accessToken: String) -> Unit,
    onError: (message: String) -> Unit,
    onCancel: () -> Unit,
): () -> Unit {
    // TODO: Wire FBSDKLoginManager via Objective-C interop
    return {}
}
