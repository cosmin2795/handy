package com.handy.feature.auth

import android.app.Activity
import android.content.Context
import android.content.Intent

/**
 * Holds the current Activity reference. Set in MainActivity.
 */
object ActivityProvider {
    var currentActivity: Activity? = null
}

/**
 * Holds the application context. Set from HandyApplication.
 */
object AndroidContextProvider {
    lateinit var applicationContext: Context
}

/**
 * Minimal shim to wire Google Sign-In's activity-result callback.
 * Replace with the proper ActivityResultContracts approach in your activity.
 */
object ActivityResultRegistry {
    fun register(activity: Activity, callback: (Intent?) -> Unit): ActivityResultLauncher =
        ActivityResultLauncher(activity, callback)
}

class ActivityResultLauncher(
    private val activity: Activity,
    val callback: (Intent?) -> Unit,
) {
    private val requestCode = 1001

    fun launch(intent: Intent) {
        @Suppress("DEPRECATION")
        activity.startActivityForResult(intent, requestCode)
    }
}
