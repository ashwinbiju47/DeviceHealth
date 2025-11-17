package com.example.devicehealth


import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class DeviceHealthApp : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
        ServiceLocator.init(this)
        Log.d("Trek Timer App", "Firebase initialized correctly")
    }
}
