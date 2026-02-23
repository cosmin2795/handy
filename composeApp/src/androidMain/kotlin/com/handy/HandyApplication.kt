package com.handy

import android.app.Application
import com.facebook.FacebookSdk

class HandyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(applicationContext)
    }
}
