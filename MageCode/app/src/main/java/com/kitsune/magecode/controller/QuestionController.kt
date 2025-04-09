package com.kitsune.magecode.controller

import android.annotation.SuppressLint
import android.content.Context
import com.kitsune.magecode.model.enums.QuestionType
import com.kitsune.magecode.model.lesson.AnswerResult
import com.kitsune.magecode.model.lesson.Question
import com.kitsune.magecode.model.managers.ResultManager

class QuestionController(
    private val context: Context,
    private val onAnswerChecked: (Boolean) -> Unit
) {

    fun checkAnswer(question: Question, userInput: Any): Boolean {
        val isCorrect = when (question.type) {
            QuestionType.MULTIPLE_CHOICE -> {
                val correctAnswers = question.correctAnswer.map { getLocalizedString(it) }
                correctAnswers.contains(userInput)
            }
            QuestionType.FILL_IN_THE_BLANK -> {
                val input = userInput as? String
                val correct = question.correctAnswer.firstOrNull()
                input?.trim()?.equals(correct, ignoreCase = true) == true
            }
            QuestionType.MATCH_PAIRS -> true
            QuestionType.DRAG_ORDER -> {
                val inputList = userInput as? List<*> ?: return false
                inputList == question.correctAnswer
            }
        }

        val result = AnswerResult(question, userInput, question.correctAnswer, isCorrect)
        ResultManager.saveResult(context, result)

        onAnswerChecked(isCorrect)
        return isCorrect
    }


    @SuppressLint("DiscouragedApi")
    private fun getLocalizedString(key: String?): String {
        if (key.isNullOrEmpty()) return ""
        val resId = context.resources.getIdentifier(key, "string", context.packageName)
        return if (resId != 0) context.getString(resId) else key
    }
}
