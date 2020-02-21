package com.graham4.patientsync

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Persist data offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}