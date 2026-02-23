package com.handy

import android.app.Application
import com.facebook.FacebookSdk
import com.handy.feature.auth.AndroidContextProvider

class HandyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidContextProvider.applicationContext = applicationContext
        FacebookSdk.sdkInitialize(applicationContext)
    }
}
