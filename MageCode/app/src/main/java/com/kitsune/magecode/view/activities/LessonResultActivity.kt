package com.kitsune.magecode.view.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.kitsune.magecode.R

class LessonResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_result)

        val correct = intent.getIntExtra("correctCount", 0)
        val xp = intent.getIntExtra("earnedXp", 0)
        val level = intent.getIntExtra("newLevel", 0)
        val streak = intent.getIntExtra("updatedStreak", 0)

        findViewById<TextView>(R.id.result_correct).text = getString(R.string.correct_answers, correct)
        findViewById<TextView>(R.id.result_xp).text = getString(R.string.earned_xp, xp)
        findViewById<TextView>(R.id.result_level).text = getString(R.string.current_level, level)
        findViewById<TextView>(R.id.result_streak).text = getString(R.string.streak_count, streak)

        val toolbar = findViewById<MaterialToolbar>(R.id.result_toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}
