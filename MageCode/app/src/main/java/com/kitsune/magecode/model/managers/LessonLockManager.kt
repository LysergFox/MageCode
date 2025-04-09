package com.kitsune.magecode.model.managers

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.content.edit

object LessonLockManager {
    private const val PREF_NAME = "lesson_lock"
    private const val KEY_LAST_LESSON_DATE = "last_lesson_date"

    fun hasCompletedLessonToday(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val savedDate = prefs.getString(KEY_LAST_LESSON_DATE, null)
        val today = getCurrentDate()
        return savedDate == today
    }

    fun saveTodayAsCompleted(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit() { putString(KEY_LAST_LESSON_DATE, getCurrentDate()) }
    }

    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date())
    }
}