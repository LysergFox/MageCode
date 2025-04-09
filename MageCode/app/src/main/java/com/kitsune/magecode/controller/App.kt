package com.kitsune.magecode.controller

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kitsune.magecode.model.Account
import com.kitsune.magecode.model.lesson.Lesson
import com.kitsune.magecode.model.lesson.Question
import com.kitsune.magecode.model.repository.AccountRepository

class App : Application() {
    companion object {
        lateinit var instance: App
            private set
    }

    lateinit var currentUser: Account
    lateinit var todayLesson: Lesson

    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    val userRepo: AccountRepository
        get() = AccountRepository(db, auth.currentUser?.uid ?: "")

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        instance = this
    }

    fun loadUserData(onComplete: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                doc.toObject(Account::class.java)?.let {
                    currentUser = it
                    onComplete()
                }
            }
    }

    fun generateTodayLesson(onComplete: (Lesson) -> Unit) {
        val selectedLanguages = currentUser.selectedLanguages.map { it.lowercase() }

        db.collection("questions")
            .whereIn("language", selectedLanguages)
            .get()
            .addOnSuccessListener { result ->
                val questions = result.documents.mapNotNull { it.toObject(Question::class.java) }
                val selectedQuestions = questions.shuffled().take(5)

                todayLesson = Lesson(
                    id = "daily_${System.currentTimeMillis()}",
                    questions = selectedQuestions,
                    xpReward = 5
                )

                onComplete(todayLesson)
            }
    }
}
