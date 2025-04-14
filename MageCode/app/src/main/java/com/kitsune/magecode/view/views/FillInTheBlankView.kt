package com.kitsune.magecode.view.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.widget.*
import com.kitsune.magecode.R
import com.kitsune.magecode.controller.QuestionController
import com.kitsune.magecode.model.lesson.Question

@SuppressLint("ViewConstructor", "UseCompatLoadingForDrawables")
class FillInTheBlankView(
    context: Context,
    private val question: Question,
    private val controller: QuestionController
) : LinearLayout(context) {

    init {
        orientation = VERTICAL
        setPadding(32, 32, 32, 32)
        background = context.getDrawable(R.drawable.lesson_background)

        val localizedPrompt = getStringByKey(question.prompt)
        val localizedSentence = getStringByKey(question.sentence)

        val sentenceParts = localizedSentence.split("__")
        val sentenceLayout = LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = android.view.Gravity.CENTER
        }

        val beforeText = TextView(context).apply {
            text = sentenceParts.getOrElse(0) { "" }
            textSize = 16f
            typeface = resources.getFont(R.font.compaspro)
            setTextColor(resources.getColor(R.color.white))
            setShadowLayer(3f, 0f, 0f, resources.getColor(R.color.black))
        }

        val inputField = EditText(context).apply {
            hint = context.getString(R.string.fill_blank_hint)
            gravity = android.view.Gravity.CENTER
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
            setPadding(16, 8, 16, 8)
            setTextColor(resources.getColor(R.color.white))
            setHintTextColor(resources.getColor(R.color.grey_200))
            typeface = resources.getFont(R.font.compaspro)
        }

        val afterText = TextView(context).apply {
            text = sentenceParts.getOrElse(1) { "" }
            textSize = 16f
            typeface = resources.getFont(R.font.compaspro)
            setTextColor(resources.getColor(R.color.white))
            setShadowLayer(3f, 0f, 0f, resources.getColor(R.color.black))
        }

        sentenceLayout.addView(beforeText)
        sentenceLayout.addView(inputField)
        sentenceLayout.addView(afterText)

        val resultText = TextView(context).apply {
            visibility = GONE
            textSize = 16f
            setPadding(0, 24, 0, 0)
            typeface = resources.getFont(R.font.compaspro)
        }

        val checkButton = Button(context).apply {
            text = context.getString(R.string.check)
            background = context.getDrawable(R.drawable.stone_button)
            setTextColor(Color.WHITE)
            textSize = 16f
            setPadding(32, 16, 32, 16)
            typeface = resources.getFont(R.font.compaspro)

            setOnClickListener {
                val userAnswer = inputField.text.toString().trim()
                val isCorrect = controller.checkAnswer(question, userAnswer)

                resultText.apply {
                    visibility = VISIBLE
                    text = if (isCorrect) context.getString(R.string.answer_correct)
                    else context.getString(R.string.answer_incorrect)
                    setTextColor(
                        if (isCorrect) resources.getColor(R.color.green_500)
                        else resources.getColor(R.color.red_500)
                    )
                }

                inputField.isEnabled = false
                this.isEnabled = false
            }
        }

        val promptView = TextView(context).apply {
            text = localizedPrompt
            textSize = 20f
            setPadding(0, 0, 0, 32)
            typeface = resources.getFont(R.font.compaspro)
            setTextColor(resources.getColor(R.color.white))
            setShadowLayer(3f, 0f, 0f, resources.getColor(R.color.black))
        }

        addView(promptView)
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
