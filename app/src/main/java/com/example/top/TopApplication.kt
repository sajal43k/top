package com.example.top

import android.app.Application
import com.google.firebase.FirebaseApp

class TopApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
