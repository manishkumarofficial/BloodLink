package com.bloodlink.app

import android.app.Application
import android.util.Log

class BloodLinkApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.i("BloodLinkApplication", "Application onCreate: Initializing application-wide configurations...")
        FirebaseConfigService.initialize(this)
    }
}
