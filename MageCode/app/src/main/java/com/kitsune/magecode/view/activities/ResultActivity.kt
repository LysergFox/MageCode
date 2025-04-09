package com.kitsune.magecode.view.activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kitsune.magecode.R
import com.kitsune.magecode.model.managers.ResultManager
import com.google.android.material.card.MaterialCardView
import androidx.core.graphics.toColorInt
import com.kitsune.magecode.model.lesson.KeyValuePair

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val container = findViewById<LinearLayout>(R.id.results_container)
        val results = ResultManager.getSavedResults(this)
        Log.d("ResultActivity",results.toString())
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
                text = getString(R.string.result_question, getLocalized(result.question.prompt))
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.BLACK)
            }

            val userAnswer = TextView(this).apply {
                text = getString(R.string.result_your_answer, formatAnswer(result.userAnswer))
                setTextColor(Color.DKGRAY)
            }

            val correctAnswer = TextView(this).apply {
                text = getString(R.string.result_correct_answer, formatAnswer(result.question.correctAnswer))
                setTextColor(Color.DKGRAY)
            }

            val correctness = TextView(this).apply {
                text = if (result.isCorrect) getString(R.string.result_correct)
                else getString(R.string.result_incorrect)
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

    @SuppressLint("DiscouragedApi")
    private fun getLocalized(key: String?): String {
        if (key.isNullOrEmpty()) return ""
        val resId = resources.getIdentifier(key, "string", packageName)
        return if (resId != 0) getString(resId) else key
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun formatAnswer(answer: Any?): String {
        return when (answer) {
            null -> "–"
            is List<*> -> answer.joinToString(", ") {
                when (it) {
                    is KeyValuePair -> "${getStringByKey(it.key)} = ${getStringByKey(it.value)}"
                    is Map<*, *> -> {
                        val key = it["first"]?.toString() ?: ""
                        val value = it["second"]?.toString() ?: ""
                        "${getStringByKey(key)} = ${getStringByKey(value)}"
                    }
                    else -> it.toString()
                }
            }
            is Map<*, *> -> answer.entries.joinToString(", ") {
                "${getStringByKey(it.key.toString())} = ${getStringByKey(it.value.toString())}"
            }
            else -> getStringByKey(answer.toString())
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun getStringByKey(key: String): String {
        val resId = resources.getIdentifier(key, "string", packageName)
        return if (resId != 0) getString(resId) else key
    }
}

