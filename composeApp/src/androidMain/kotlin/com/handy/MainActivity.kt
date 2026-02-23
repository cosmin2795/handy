package com.handy

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.facebook.CallbackManager
import com.handy.feature.auth.ActivityProvider
import com.handy.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.compose.KoinContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {

    private val facebookCallbackManager: CallbackManager by lazy {
        CallbackManager.Factory.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ActivityProvider.currentActivity = this

        startKoin {
            androidContext(applicationContext)
            modules(appModules)
        }

        setContent {
            KoinContext {
                App()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ActivityProvider.currentActivity = this
    }

    override fun onPause() {
        super.onPause()
        if (ActivityProvider.currentActivity == this) {
            ActivityProvider.currentActivity = null
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
