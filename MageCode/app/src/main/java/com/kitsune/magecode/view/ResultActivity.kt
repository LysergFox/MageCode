package com.kitsune.magecode.view

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kitsune.magecode.R
import com.kitsune.magecode.model.ResultManager
import com.google.android.material.card.MaterialCardView
import androidx.core.graphics.toColorInt

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val container = findViewById<LinearLayout>(R.id.results_container)
        val results = ResultManager.getSavedResults(this)

        results.forEach { result ->
            val cardView = MaterialCardView(this).apply {
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, dpToPx(16))
                }

                setCardElevation(dpToPx(4).toFloat())
                radius = dpToPx(8).toFloat()
                setCardBackgroundColor(
                    if (result.isCorrect) "#E6F4EA".toColorInt()
                    else "#FCE8E6".toColorInt()
                )
            }

            val cardContent = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16))
            }

            val questionText = TextView(this).apply {
                text = "Q: ${result.question.prompt}"
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.BLACK)
            }

            val userAnswer = TextView(this).apply {
                text = "Your Answer: ${formatAnswer(result.userAnswer)}"
                setTextColor(Color.DKGRAY)
            }

            val correctAnswer = TextView(this).apply {
                text = "Correct Answer: ${formatAnswer(result.question.correctAnswer)}"
                setTextColor(Color.DKGRAY)
            }

            val correctness = TextView(this).apply {
                text = if (result.isCorrect) "✔ Correct" else "✖ Incorrect"
                setTextColor(if (result.isCorrect) Color.GREEN else Color.RED)
                setTypeface(null, Typeface.BOLD)
            }

            cardContent.apply {
                addView(questionText)
                addView(userAnswer)
                    addView(correctAnswer)

                addView(correctness)
            }

            cardView.addView(cardContent)
            container.addView(cardView)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun formatAnswer(answer: Any?): String {
        return when (answer) {
            null -> "–"
            is List<*> -> answer.joinToString(", ", "", "") { it.toString() }
            is Map<*, *> -> answer.entries.joinToString(", ", "", "") {
                "${it.key} = ${it.value}"
            }
            else -> answer.toString()
        }
    }
}
