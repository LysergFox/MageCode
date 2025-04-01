package com.kitsune.magecode.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kitsune.magecode.R
import com.kitsune.magecode.controller.LoginController

class LoginActivity : AppCompatActivity() {

    private lateinit var loginController: LoginController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginController = LoginController(this)

        findViewById<Button>(R.id.google_sign_in_button).setOnClickListener {
            loginController.startGoogleSignIn()
        }
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