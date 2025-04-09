package com.kitsune.magecode.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth
import com.kitsune.magecode.R
import com.kitsune.magecode.controller.App
import com.kitsune.magecode.controller.LoginController

class LoginActivity : AppCompatActivity() {

    private var isUserDataLoaded = false
    private lateinit var loginController: LoginController
    private val auth = FirebaseAuth.getInstance()
    private val appController = App.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen: SplashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !isUserDataLoaded }

        super.onCreate(savedInstanceState)
        if (auth.currentUser != null) {
            appController.loadUserData {
                isUserDataLoaded = true
                goToDashboard()
            }
        } else {
            isUserDataLoaded = true
            setContentView(R.layout.activity_login)
            setupLogin()
        }
    }

    private fun setupLogin() {
        loginController = LoginController(this)

        findViewById<Button>(R.id.google_sign_in_button).setOnClickListener {
            loginController.startGoogleSignIn()
        }
    }

    private fun goToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == loginController.getRequestCode()) {
            loginController.handleSignInResult(
                data,
                onSuccess = {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                },
                onFailure = { error ->
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

}