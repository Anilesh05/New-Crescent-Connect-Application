package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.data.local.CrescentDatabase
import com.example.data.local.DatabaseSeeder
import com.example.ui.navigation.CrescentConnectApp
import com.example.ui.theme.CrescentConnectTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.data.local.SessionManager


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val db = CrescentDatabase.getDatabase(this)
        DatabaseSeeder.seedDatabase(db)
        
                val sessionManager = SessionManager(this)

        setContent {
            val themePreference by sessionManager.themeFlow.collectAsState(initial = "system")
            val isDarkTheme = when (themePreference) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }
            CrescentConnectTheme(darkTheme = isDarkTheme) {
                CrescentConnectApp(modifier = Modifier.fillMaxSize(), context = this)
            }
        }
    }
}
