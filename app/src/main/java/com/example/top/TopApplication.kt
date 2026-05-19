package com.example.top

import android.app.Application
import com.google.firebase.FirebaseApp

class TopApplication : Application() {
    companion object {
        lateinit var instance: TopApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        FirebaseApp.initializeApp(this)
    }
}
