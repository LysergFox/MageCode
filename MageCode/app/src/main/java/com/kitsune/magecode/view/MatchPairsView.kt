package com.kitsune.magecode.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.kitsune.magecode.controller.QuestionController
import com.kitsune.magecode.model.lesson.Question
import androidx.core.graphics.drawable.toDrawable

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

    init {
        orientation = VERTICAL
        setPadding(24, 24, 24, 24)

        val promptText = TextView(context).apply {
            text = question.prompt
            textSize = 18f
            setPadding(8, 8, 8, 16)
            gravity = Gravity.CENTER
        }
        addView(promptText)

        val container = LinearLayout(context).apply {
            orientation = HORIZONTAL
        }

        val leftColumn = LinearLayout(context).apply {
            orientation = VERTICAL
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
        }

        val rightColumn = LinearLayout(context).apply {
            orientation = VERTICAL
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
        }

        val pairs = question.pairs ?: emptyList()

        val shuffledLeft = pairs.map { it.key }.shuffled()
        val shuffledRight = pairs.map { it.value }.shuffled()

        shuffledLeft.forEach { item ->
            val view = createSelectableItem(item, isLeft = true)
            leftColumn.addView(view)
            leftItems[item] = view
        }

        shuffledRight.forEach { item ->
            val view = createSelectableItem(item, isLeft = false)
            rightColumn.addView(view)
            rightItems[item] = view
        }

        container.addView(leftColumn)
        container.addView(rightColumn)
        addView(container)
    }

    private fun createSelectableItem(text: String, isLeft: Boolean): TextView {
        return TextView(context).apply {
            this.text = text
            textSize = 16f
            setPadding(16, 16, 16, 16)
            background = Color.LTGRAY.toDrawable()
            setTextColor(Color.BLACK)
            val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            lp.setMargins(8, 8, 8, 8)
            layoutParams = lp
            setOnClickListener {
                if (isLeft) {
                    if (selectedLeft == this) {
                        selectedLeft = null
                        background = Color.LTGRAY.toDrawable()
                    } else {
                        selectedLeft?.background = Color.LTGRAY.toDrawable()
                        selectedLeft = this
                        background = Color.YELLOW.toDrawable()
                    }
                } else {
                    if (selectedRight == this) {
                        selectedRight = null
                        background = Color.LTGRAY.toDrawable()
                    } else {
                        selectedRight?.background = Color.LTGRAY.toDrawable()
                        selectedRight = this
                        background = Color.YELLOW.toDrawable()
                    }
                }

                if (selectedLeft != null && selectedRight != null) {
                    val leftText = selectedLeft!!.text.toString()
                    val rightText = selectedRight!!.text.toString()
                    val isCorrect = question.pairs!!.any { it.key == leftText && it.value == rightText }

                    if (isCorrect) {
                        matched.add(Pair(leftText, rightText))
                        selectedLeft!!.background = Color.GREEN.toDrawable()
                        selectedRight!!.background = Color.GREEN.toDrawable()
                        selectedLeft!!.isClickable = false
                        selectedRight!!.isClickable = false
                    } else {
                        Toast.makeText(context, "Incorrect match", Toast.LENGTH_SHORT).show()
                        selectedLeft!!.background = Color.LTGRAY.toDrawable()
                        selectedRight!!.background = Color.LTGRAY.toDrawable()
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
}
