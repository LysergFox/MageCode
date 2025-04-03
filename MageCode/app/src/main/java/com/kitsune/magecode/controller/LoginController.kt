@file:Suppress("DEPRECATION")

package com.kitsune.magecode.controller

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.kitsune.magecode.model.Account
import java.util.Date

class LoginController(private val activity: Activity) {

    private val auth = App.instance.auth
    private val db = App.instance.db
    private lateinit var googleSignInClient: GoogleSignInClient

    private val RC_SIGN_IN = 1001

    fun startGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(com.kitsune.magecode.R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)

        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun handleSignInResult(
        data: Intent?,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)

        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                signInWithFirebase(idToken, onSuccess, onFailure)
            } else {
                onFailure("ID token was null.")
            }
        } catch (e: ApiException) {
            onFailure("Google Sign-In failed: ${e.message}")
        }
    }

    private fun signInWithFirebase(
        idToken: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnSuccessListener { result ->
                val user = result.user ?: return@addOnSuccessListener onFailure("No Firebase user")

                val userRef = db.collection("users").document(user.uid)

                userRef.get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(Account::class.java)?.let {
                            App.instance.currentUser = it
                            onSuccess()
                        } ?: onFailure("Failed to parse account")
                    } else {
                        val newAccount = Account(
                            uid = user.uid,
                            displayName = user.displayName ?: "",
                            email = user.email ?: "",
                            selectedLanguages = listOf("Python"),
                            level = 1,
                            xp = 0,
                            streak = 0,
                            lastActive = Date()
                        )

                        userRef.set(newAccount)
                            .addOnSuccessListener {
                                App.instance.currentUser = newAccount
                                onSuccess()
                            }
                            .addOnFailureListener { onFailure(it.message ?: "Error saving user") }
                    }
                }
            }
            .addOnFailureListener { onFailure("Firebase sign-in failed: ${it.message}") }
    }

    fun getRequestCode(): Int = RC_SIGN_IN
}
