package com.kitsune.magecode.model.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kitsune.magecode.model.Account
import com.kitsune.magecode.model.enums.SortOption

class AccountRepository (private val db: FirebaseFirestore, private val uid: String) {
    fun updateLanguages(languages: List<String>, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        db.collection("users").document(uid)
            .update("selectedLanguages", languages)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "Failed to update") }
    }
    fun fetchTopPlayers(
        sortBy: SortOption = SortOption.XP,
        limit: Long = 20,
        onSuccess: (List<Account>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("users")
            .orderBy(sortBy.field, Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .addOnSuccessListener { result ->
                val players = result.documents.mapNotNull { it.toObject(Account::class.java) }
                onSuccess(players)
            }
            .addOnFailureListener {
                onFailure(it.message ?: "Failed to fetch top players")
            }
    }
}