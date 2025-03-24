package com.kitsune.magecode.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.kitsune.magecode.R
import com.kitsune.magecode.controller.App

class DashboardActivity : AppCompatActivity() {
    private val appController = App.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        appController.loadUserData {
            // Update UI after loading
        }

        appController.generateTodayLesson {
            // Launch lesson
        }
    }
}