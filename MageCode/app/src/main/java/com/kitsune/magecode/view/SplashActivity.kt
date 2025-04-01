package com.kitsune.magecode.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth
import com.kitsune.magecode.controller.App

class SplashActivity : ComponentActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val appController = App.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        // ✅ Show system splash screen first
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // ✅ Immediate logic - no manual delay needed
        if (auth.currentUser != null) {
            appController.loadUserData {
                goToDashboard()
            }
        } else {
            goToLogin()
        }
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun goToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
