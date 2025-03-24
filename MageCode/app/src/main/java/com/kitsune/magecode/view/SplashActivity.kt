package com.kitsune.magecode.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.kitsune.magecode.R
import com.kitsune.magecode.controller.App
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private val splashDelay: Long = 1500 // 1.5 seconds to show logo/animation
    private val appController = App.instance
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            if (auth.currentUser != null) {
                appController.loadUserData {
                    goToDashboard()
                }
            } else {
                goToLogin()
            }
        }, splashDelay)
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
