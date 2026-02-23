package com.handy.feature.auth.ui

import androidx.compose.runtime.Composable

/**
 * Returns a lambda that launches the platform-native Google sign-in flow.
 * On success [onToken] is called with the Google ID token.
 * On failure [onError] is called with a human-readable message.
 * On user cancellation [onCancel] is called.
 */
@Composable
expect fun rememberGoogleAuthLauncher(
    onToken: (idToken: String) -> Unit,
    onError: (message: String) -> Unit,
    onCancel: () -> Unit,
): () -> Unit
