package com.kitsune.magecode.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.kitsune.magecode.R
import com.kitsune.magecode.controller.App

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

        appController.generateTodayLesson {
        }
    }
}
