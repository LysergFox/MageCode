package com.kitsune.magecode.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.kitsune.magecode.R
import com.kitsune.magecode.controller.App
import com.kitsune.magecode.model.ResultManager
import java.util.Locale
import androidx.core.content.edit
import com.kitsune.magecode.model.LessonLockManager

@Suppress("DEPRECATION")
class DashboardActivity : AppCompatActivity() {
    private val appController = App.instance

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.topAppBar)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val spinnerItem = navView.menu.findItem(R.id.nav_language_spinner)
        val spinner = spinnerItem.actionView as Spinner

        val languageNames = resources.getStringArray(R.array.language_names)
        val languageCodes = resources.getStringArray(R.array.language_codes)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languageNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val sharedPref = getSharedPreferences("settings", MODE_PRIVATE)
        val savedCode = sharedPref.getString("language", "en")
        val selectedIndex = languageCodes.indexOf(savedCode)
        spinner.setSelection(if (selectedIndex != -1) selectedIndex else 0)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCode = languageCodes[position]
                val currentCode = sharedPref.getString("language", "")
                if (selectedCode != currentCode) {
                    sharedPref.edit { putString("language", selectedCode) }
                    changeAppLocale(selectedCode)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_compendium -> {
                    Toast.makeText(this, "Opening Compendium...", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        appController.loadUserData {
            val menu = navView.menu
            menu.findItem(R.id.nav_xp)?.title = "XP: ${appController.currentUser.xp}"
            menu.findItem(R.id.nav_level)?.title = "Level: ${appController.currentUser.level}"
        }

        val startLessonButton = findViewById<Button>(R.id.start_lesson_btn)

        appController.generateTodayLesson { lesson ->
            startLessonButton.setOnClickListener {
                //if (LessonLockManager.hasCompletedLessonToday(this)) {
                    Toast.makeText(this, "You've already completed today's lesson. Come back tomorrow!", Toast.LENGTH_SHORT).show()
                //} else {
                    LessonLockManager.saveTodayAsCompleted(this)
                    ResultManager.clearResults(this)

                    val intent = Intent(this, LessonActivity::class.java)
                    intent.putExtra("lesson_id", lesson.id)
                    startActivity(intent)
                //}
            }
        }

        val viewResultsBtn = findViewById<Button>(R.id.view_results_btn)

        viewResultsBtn.setOnClickListener {
            startActivity(Intent(this, ResultActivity::class.java))
        }
    }

    private fun changeAppLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        recreate()
    }

    override fun onResume() {
        super.onResume()
        updateResultsButton()
    }

    private fun updateResultsButton() {
        val viewResultsBtn = findViewById<Button>(R.id.view_results_btn)
        val hasResults = ResultManager.getSavedResults(this).isNotEmpty()
        viewResultsBtn.visibility = if (hasResults) View.VISIBLE else View.GONE
    }

}
