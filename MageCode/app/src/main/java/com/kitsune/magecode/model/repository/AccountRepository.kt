package com.kitsune.magecode.model.repository

import com.google.firebase.firestore.FirebaseFirestore

class AccountRepository (private val db: FirebaseFirestore, private val uid: String) {
    fun updateLanguages(languages: List<String>, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        db.collection("users").document(uid)
            .update("selectedLanguages", languages)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "Failed to update") }
    }
}