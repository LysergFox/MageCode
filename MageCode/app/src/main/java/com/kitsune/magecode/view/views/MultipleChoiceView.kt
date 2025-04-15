package com.kitsune.magecode.view.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.widget.*
import com.kitsune.magecode.R
import com.kitsune.magecode.controller.QuestionController
import com.kitsune.magecode.model.lesson.Question

@SuppressLint("ViewConstructor", "UseCompatLoadingForDrawables")
class MultipleChoiceView(
    context: Context,
    private val question: Question,
    private val controller: QuestionController
) : LinearLayout(context) {

    private var answered = false

    init {
        orientation = VERTICAL
        setPadding(32, 32, 32, 32)
        background = context.getDrawable(R.drawable.lesson_background)

        val prompt = TextView(context).apply {
            text = getLocalizedString(question.prompt)
            textSize = 20f
            setTextColor(Color.WHITE)
            typeface = resources.getFont(R.font.compaspro)
            setShadowLayer(3f, 0f, 0f, Color.BLACK)
            setPadding(16, 16, 16, 32)
        }
        addView(prompt)

        question.options.shuffled().forEach { optionKey ->
            val button = Button(context).apply {
                text = getLocalizedString(optionKey)
                tag = optionKey
                background = context.getDrawable(R.drawable.stone_button)
                setTextColor(Color.WHITE)
                textSize = 16f
                typeface = resources.getFont(R.font.compaspro)
                setShadowLayer(3f, 0f, 0f, Color.BLACK)
                setPadding(32, 24, 32, 24)

                val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                lp.setMargins(0, 0, 0, 24)
                layoutParams = lp

                setOnClickListener {
                    if (answered) return@setOnClickListener
                    answered = true

                    val isCorrect = controller.checkAnswer(question, text)
                    background = if (isCorrect)
                        context.getDrawable(R.drawable.option_correct)
                    else
                        context.getDrawable(R.drawable.option_incorrect)

                    disableAllButtons()
                }
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
