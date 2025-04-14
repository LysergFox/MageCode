package com.kitsune.magecode.view.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import androidx.core.graphics.drawable.toDrawable
import com.kitsune.magecode.R
import com.kitsune.magecode.controller.QuestionController
import com.kitsune.magecode.model.lesson.Question
import com.kitsune.magecode.view.CustomComponents

@SuppressLint("ViewConstructor")
class MatchPairsView(
    context: Context,
    private val question: Question,
    private val controller: QuestionController
) : LinearLayout(context) {

    private val matched = mutableListOf<Pair<String, String>>()
    private var selectedLeft: TextView? = null
    private var selectedRight: TextView? = null
    private val leftItems = mutableMapOf<String, TextView>()
    private val rightItems = mutableMapOf<String, TextView>()
    private val customComponents = CustomComponents

    init {
        orientation = VERTICAL
        setPadding(32, 32, 32, 32)
        background = context.getDrawable(R.drawable.lesson_background)

        val promptText = TextView(context).apply {
            text = getLocalizedString(question.prompt)
            textSize = 20f
            setPadding(8, 8, 8, 24)
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            typeface = resources.getFont(R.font.compaspro)
            setShadowLayer(3f, 0f, 0f, Color.BLACK)
        }
        addView(promptText)

        val container = LinearLayout(context).apply {
            orientation = HORIZONTAL
        }

        val leftColumn = LinearLayout(context).apply {
            orientation = VERTICAL
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
            gravity = Gravity.CENTER
        }

        val rightColumn = LinearLayout(context).apply {
            orientation = VERTICAL
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
            gravity = Gravity.CENTER
        }

        val pairs = question.pairs ?: emptyList()
        val shuffledLeft = pairs.map { it.key }.shuffled()
        val shuffledRight = pairs.map { it.value }.shuffled()

        shuffledLeft.forEach { key ->
            val view = createSelectableItem(getLocalizedString(key), isLeft = true, originalKey = key)
            leftColumn.addView(view)
            leftItems[key] = view
        }

        shuffledRight.forEach { value ->
            val view = createSelectableItem(getLocalizedString(value), isLeft = false, originalKey = value)
            rightColumn.addView(view)
            rightItems[value] = view
        }

        container.addView(leftColumn)
        container.addView(rightColumn)
        addView(container)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun createSelectableItem(displayText: String, isLeft: Boolean, originalKey: String): TextView {
        return TextView(context).apply {
            tag = originalKey
            text = displayText
            textSize = 16f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            typeface = resources.getFont(R.font.compaspro)
            setShadowLayer(3f, 0f, 0f, Color.BLACK)
            background = context.getDrawable(R.drawable.stone_button)
            setPadding(24, 16, 24, 16)

            val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            lp.setMargins(8, 8, 8, 8)
            layoutParams = lp

            setOnClickListener {
                if (isLeft) {
                    handleLeftSelection(this)
                } else {
                    handleRightSelection(this)
                }

                if (selectedLeft != null && selectedRight != null) {
                    val leftRaw = selectedLeft!!.tag.toString()
                    val rightRaw = selectedRight!!.tag.toString()
                    val isCorrect = question.pairs!!.any { it.key == leftRaw && it.value == rightRaw }

                    if (isCorrect) {
                        matched.add(Pair(leftRaw, rightRaw))
                        selectedLeft!!.background = context.getDrawable(R.drawable.option_correct)
                        selectedRight!!.background = context.getDrawable(R.drawable.option_correct)
                        selectedLeft!!.isClickable = false
                        selectedRight!!.isClickable = false
                    } else {
                        customComponents.showStoneToast(context, context.getString(R.string.incorrect_match))
                        selectedLeft!!.background = context.getDrawable(R.drawable.stone_button)
                        selectedRight!!.background = context.getDrawable(R.drawable.stone_button)
                    }

                    selectedLeft = null
                    selectedRight = null

                    if (matched.size == question.pairs.size) {
                        controller.checkAnswer(question, matched)
                    }
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun handleLeftSelection(view: TextView) {
        if (selectedLeft == view) {
            selectedLeft = null
            view.background = context.getDrawable(R.drawable.stone_button)
        } else {
            selectedLeft?.background = context.getDrawable(R.drawable.stone_button)
            selectedLeft = view
            view.background = Color.YELLOW.toDrawable()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun handleRightSelection(view: TextView) {
        if (selectedRight == view) {
            selectedRight = null
            view.background = context.getDrawable(R.drawable.stone_button)
        } else {
            selectedRight?.background = context.getDrawable(R.drawable.stone_button)
            selectedRight = view
            view.background = Color.YELLOW.toDrawable()
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun getLocalizedString(key: String): String {
        val resId = resources.getIdentifier(key, "string", context.packageName)
        return if (resId != 0) context.getString(resId) else key
    }
}
