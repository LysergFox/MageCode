package com.kitsune.magecode.controller

import android.app.Activity
import android.os.CancellationSignal
import androidx.credentials.*
import androidx.credentials.exceptions.*
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.kitsune.magecode.model.Account
import java.util.Date
import java.util.concurrent.Executor
import com.kitsune.magecode.R

class LoginController(private val activity: Activity) {

    private val auth = App.instance.auth
    private val db = App.instance.db
    private val credentialManager = CredentialManager.create(activity)

    fun launchGoogleSignIn(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val request = GetCredentialRequest(
            listOf(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(activity.getString(R.string.default_web_client_id))
                    .build()
            )
        )

        val signal = CancellationSignal()
        val executor: Executor = activity.mainExecutor

        credentialManager.getCredentialAsync(
            context = activity,
            request = request,
            cancellationSignal = signal,
            executor = executor,
            callback = object : CredentialManagerCallback<GetCredentialResponse, GetCredentialException> {
                override fun onResult(result: GetCredentialResponse) {
                    val credential = result.credential
                    if (credential is GoogleIdTokenCredential) {
                        val idToken = credential.idToken
                        if (!idToken.isNullOrEmpty()) {
                            signInWithFirebase(idToken, onSuccess, onFailure)
                        } else {
                            onFailure("Missing ID token.")
                        }
                    } else {
                        onFailure("Unsupported credential type.")
                    }
                }

                override fun onError(e: GetCredentialException) {
                    onFailure("Google Sign-In failed: ${e.message}")
                }
            }
        )
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

                userRef.get().addOnSuccessListener { docSnapshot ->
                    if (docSnapshot.exists()) {
                        val account = docSnapshot.toObject(Account::class.java)
                        if (account != null) {
                            App.instance.currentUser = account
                            onSuccess()
                        } else {
                            onFailure("Failed to parse user data")
                        }

                    } else {
                        // New user → Create default account
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
                            .addOnFailureListener { onFailure("Failed to create user: ${it.message}") }
                    }
                }
            }
            .addOnFailureListener { onFailure("Firebase sign-in failed: ${it.message}") }
    }

}
