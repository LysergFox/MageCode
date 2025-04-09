package com.kitsune.magecode.view.views

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.kitsune.magecode.controller.QuestionController
import com.kitsune.magecode.model.lesson.Question

@SuppressLint("ViewConstructor")
class MultipleChoiceView(
    context: Context,
    private val question: Question,
    private val controller: QuestionController
) : LinearLayout(context) {

    private var answered = false

    init {
        orientation = VERTICAL

        val prompt = TextView(context).apply {
            text = getLocalizedString(question.prompt)
            textSize = 18f
            setPadding(16, 16, 16, 32)
        }
        addView(prompt)

        question.options.forEach { optionKey ->
            val button = Button(context).apply {
                text = getLocalizedString(optionKey)
                setOnClickListener {
                    if (answered) return@setOnClickListener
                    answered = true

                    val isCorrect = controller.checkAnswer(question, text)
                    setBackgroundColor(
                        if (isCorrect) android.graphics.Color.GREEN
                        else android.graphics.Color.RED
                    )

                    disableAllButtons()
                }
                tag = optionKey
            }
            addView(button)
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun getLocalizedString(key: String): String {
        val resId = resources.getIdentifier(key, "string", context.packageName)
        return if (resId != 0) context.getString(resId) else key
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
