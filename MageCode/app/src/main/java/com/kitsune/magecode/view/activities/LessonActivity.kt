package com.kitsune.magecode.view.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.kitsune.magecode.R
import com.kitsune.magecode.controller.App
import com.kitsune.magecode.controller.QuestionController
import com.kitsune.magecode.model.lesson.Question
import com.kitsune.magecode.view.CustomComponents
import com.kitsune.magecode.view.factory.QuestionViewFactory

class LessonActivity : AppCompatActivity() {
    private lateinit var questions: List<Question>
    private var currentIndex = 0
    private lateinit var questionContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)

        onBackPressedDispatcher.addCallback(this) {
            // Do nothing to disable back gesture
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val exclusionZone = android.graphics.Rect(0, 0, 300, resources.displayMetrics.heightPixels)
            window.decorView.systemGestureExclusionRects = listOf(exclusionZone)
        }

        questions = App.instance.todayLesson.questions

        questionContainer = findViewById(R.id.question_container)

        showQuestion()

        findViewById<Button>(R.id.next_question_btn).setOnClickListener {
            if (currentIndex < questions.size - 1) {
                currentIndex++
                showQuestion()
            } else {
                finishLesson()
            }
        }
    }

    private fun showQuestion() {
        val question = questions[currentIndex]
        val view = QuestionViewFactory.createView(question, this, QuestionController(this) {
        })

        questionContainer.removeAllViews()
        questionContainer.addView(view)
    }

    private fun finishLesson() {
        val results = com.kitsune.magecode.model.managers.ResultManager.getSavedResults(this)
        val correctCount = results.count { it.isCorrect }
        val earnedXp = correctCount * 9

        App.instance.userRepo.updateAfterLesson(
            earnedXp = earnedXp,
            onSuccess = {
                val newTotalXp = App.instance.currentUser.xp + earnedXp
                val newLevel = (newTotalXp / 100) + 1
                val updatedStreak = App.instance.currentUser.streak + 1

                val intent = Intent(this, LessonResultActivity::class.java).apply {
                    putExtra("correctCount", correctCount)
                    putExtra("earnedXp", earnedXp)
                    putExtra("newLevel", newLevel)
                    putExtra("updatedStreak", updatedStreak)
                }
                startActivity(intent)
                finish()
            },
            onFailure = {
                CustomComponents.showStoneToast(this, "Failed to update account: $it")
            }
        )
    }
}

