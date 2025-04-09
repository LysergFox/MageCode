package com.kitsune.magecode.view.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.widget.*
import com.kitsune.magecode.R
import com.kitsune.magecode.controller.QuestionController
import com.kitsune.magecode.model.lesson.Question

@SuppressLint("ViewConstructor")
class FillInTheBlankView(
    context: Context,
    private val question: Question,
    private val controller: QuestionController
) : LinearLayout(context) {

    init {
        orientation = VERTICAL
        setPadding(32, 32, 32, 32)

        val localizedPrompt = getStringByKey(question.prompt)
        val localizedSentence = getStringByKey(question.sentence)

        val sentenceParts = localizedSentence.split("__")
        val sentenceLayout = LinearLayout(context).apply {
            orientation = HORIZONTAL
        }

        val beforeText = TextView(context).apply {
            text = sentenceParts.getOrElse(0) { "" }
            textSize = 16f
        }

        val inputField = EditText(context).apply {
            hint = context.getString(R.string.fill_blank_hint)
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
        }

        val afterText = TextView(context).apply {
            text = sentenceParts.getOrElse(1) { "" }
            textSize = 16f
        }

        sentenceLayout.addView(beforeText)
        sentenceLayout.addView(inputField)
        sentenceLayout.addView(afterText)

        val resultText = TextView(context).apply {
            visibility = GONE
            textSize = 16f
        }

        val checkButton = Button(context).apply {
            text = context.getString(R.string.check)
            setOnClickListener {
                val userAnswer = inputField.text.toString().trim()
                val isCorrect = controller.checkAnswer(question, userAnswer)

                resultText.apply {
                    visibility = VISIBLE
                    text = if (isCorrect) context.getString(R.string.answer_correct)
                    else context.getString(R.string.answer_incorrect)
                    setTextColor(if (isCorrect) Color.GREEN else Color.RED)
                }

                inputField.isEnabled = false
                this.isEnabled = false
            }
        }

        addView(TextView(context).apply {
            text = localizedPrompt
            textSize = 18f
        })
        addView(sentenceLayout)
        addView(checkButton)
        addView(resultText)
    }

    @SuppressLint("DiscouragedApi")
    private fun getStringByKey(key: String): String {
        val resId = context.resources.getIdentifier(key, "string", context.packageName)
        return if (resId != 0) context.getString(resId) else key
    }
}
