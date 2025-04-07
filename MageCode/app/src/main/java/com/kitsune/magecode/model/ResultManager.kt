package com.kitsune.magecode.model

import android.content.Context
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.kitsune.magecode.model.lesson.AnswerResult
import androidx.core.content.edit

object ResultManager {
    private const val KEY_RESULTS = "lesson_results"

    fun saveResult(context: Context, result: AnswerResult) {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val gson = Gson()
        val current = getSavedResults(context).toMutableList()
        current.add(result)
        prefs.edit() { putString(KEY_RESULTS, gson.toJson(current)) }
    }

    fun getSavedResults(context: Context): List<AnswerResult> {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_RESULTS, null) ?: return emptyList()
        val type = object : TypeToken<List<AnswerResult>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun clearResults(context: Context) {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .edit() { remove(KEY_RESULTS) }
    }
}