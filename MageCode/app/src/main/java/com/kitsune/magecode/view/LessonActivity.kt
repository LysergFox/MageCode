package com.kitsune.magecode.view

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kitsune.magecode.R
import com.kitsune.magecode.controller.App
import com.kitsune.magecode.controller.QuestionController
import com.kitsune.magecode.model.lesson.Question
import com.kitsune.magecode.view.factory.QuestionViewFactory

class LessonActivity : AppCompatActivity() {
    private lateinit var questions: List<Question>
    private var currentIndex = 0
    private lateinit var questionContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson)

        questions = App.instance.todayLesson.questions
        Log.d("LessonActivity", "Loaded questions: ${questions.size}")

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
        Log.d("LessonActivity", "${question.type}, PROMPT: ${question.prompt}")

        questionContainer.removeAllViews()
        questionContainer.addView(view)
    }

    private fun finishLesson() {
        Toast.makeText(this, "Lesson Complete!", Toast.LENGTH_LONG).show()
        finish()
    }
}

