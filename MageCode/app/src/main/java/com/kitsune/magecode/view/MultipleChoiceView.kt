package com.kitsune.magecode.view

import android.content.Context
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.kitsune.magecode.controller.QuestionController
import com.kitsune.magecode.model.lesson.Question

class MultipleChoiceView(
    context: Context,
    private val question: Question,
    private val controller: QuestionController
) : LinearLayout(context) {

    private var answered = false

    init {
        orientation = VERTICAL
        val prompt = TextView(context).apply {
            text = question.prompt
            textSize = 18f
            setPadding(16, 16, 16, 32)
        }
        addView(prompt)

        question.options.forEach { option ->
            val button = Button(context).apply {
                text = option
                setOnClickListener {
                    if (answered) return@setOnClickListener
                    answered = true

                    val isCorrect = controller.checkAnswer(question, option)
                    setBackgroundColor(
                        if (isCorrect) android.graphics.Color.GREEN
                        else android.graphics.Color.RED
                    )

                    disableAllButtons()
                }
            }
            addView(button)
        }
    }

    private fun disableAllButtons() {
        for (i in 0 until childCount) {
            val view = getChildAt(i)
            if (view is Button) {
                view.isEnabled = false
            }
        }
    }
}

