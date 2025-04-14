package com.kitsune.magecode.view.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.kitsune.magecode.R
import com.kitsune.magecode.controller.App
import com.kitsune.magecode.model.managers.ResultManager
import java.util.Locale
import androidx.core.content.edit
import com.kitsune.magecode.model.managers.LessonLockManager
import com.kitsune.magecode.view.CustomComponents
import androidx.core.view.isVisible

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

        val languageChooserView = navView.menu.findItem(R.id.nav_language_select).actionView

        val pythonCheck = languageChooserView?.findViewById<CheckBox>(R.id.python_checkbox)
        val javaCheck = languageChooserView?.findViewById<CheckBox>(R.id.java_checkbox)
        val sqlCheck = languageChooserView?.findViewById<CheckBox>(R.id.sql_checkbox)

        val toggleText = languageChooserView?.findViewById<View>(R.id.language_toggle)
        val checkboxGroup = languageChooserView?.findViewById<LinearLayout>(R.id.language_checkboxes)

        toggleText?.setOnClickListener {
            checkboxGroup?.let {
                val visible = it.isVisible
                it.visibility = if (visible) View.GONE else View.VISIBLE
            }
        }

        appController.loadUserData {
            val selected = appController.currentUser.selectedLanguages
            pythonCheck?.isChecked = "Python" in selected
            javaCheck?.isChecked = "Java" in selected
            sqlCheck?.isChecked = "SQL" in selected

            val menu = navView.menu
            menu.findItem(R.id.nav_xp)?.title = "${appController.currentUser.xp}"
            menu.findItem(R.id.nav_level)?.title = "${appController.currentUser.level}"
            menu.findItem(R.id.nav_streak)?.title = "${appController.currentUser.streak}"
        }

        val saveSelection = {
            val newLanguages = listOfNotNull(
                if (pythonCheck?.isChecked == true) "Python" else null,
                if (javaCheck?.isChecked == true) "Java" else null,
                if (sqlCheck?.isChecked == true) "SQL" else null
            )

            appController.userRepo.updateLanguages(
                newLanguages,
                onSuccess = {
                    CustomComponents.showStoneToast(this,getString(R.string.saved_languages))
                },
                onFailure = { error ->
                    CustomComponents.showStoneToast(this,"Error: $error")
                }
            )
        }

        pythonCheck?.setOnCheckedChangeListener { _, _ -> saveSelection() }
        javaCheck?.setOnCheckedChangeListener { _, _ -> saveSelection() }
        sqlCheck?.setOnCheckedChangeListener { _, _ -> saveSelection() }

        navView.itemIconTintList = null;

        val startLessonButton = findViewById<Button>(R.id.start_lesson_btn)

        appController.generateTodayLesson { lesson ->
            startLessonButton.setOnClickListener {
                CustomComponents.showStoneToast(this,getString(R.string.completed_today))
                LessonLockManager.saveTodayAsCompleted(this)
                ResultManager.clearResults(this)

                val intent = Intent(this, LessonActivity::class.java)
                intent.putExtra("lesson_id", lesson.id)
                startActivity(intent)
            }
        }

        val viewResultsBtn = findViewById<Button>(R.id.view_results_btn)
        viewResultsBtn.setOnClickListener {
            startActivity(Intent(this, ResultActivity::class.java))
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
                    val intent = Intent(this, WebViewActivity::class.java)
                    intent.putExtra("url", "https://www.w3schools.com")
                    startActivity(intent)
                    true
                }
                R.id.nav_scoreboard -> {
                    startActivity(Intent(this, ScoreboardActivity::class.java))
                    true
                }
                else -> false
            }
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

