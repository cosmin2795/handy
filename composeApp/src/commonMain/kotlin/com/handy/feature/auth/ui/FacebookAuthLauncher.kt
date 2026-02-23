package com.handy.feature.auth.ui

import androidx.compose.runtime.Composable

/**
 * Returns a lambda that launches the platform-native Facebook login flow.
 * On success [onToken] is called with the Facebook access token.
 * On failure [onError] is called with a human-readable message.
 * On user cancellation [onCancel] is called.
 */
@Composable
expect fun rememberFacebookAuthLauncher(
    onToken: (accessToken: String) -> Unit,
    onError: (message: String) -> Unit,
    onCancel: () -> Unit,
): () -> Unit
