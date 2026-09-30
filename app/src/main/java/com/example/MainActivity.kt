package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.example.thoodhu.data.local.ThoodhuDatabase
import com.example.thoodhu.data.repository.AuthRepository
import com.example.thoodhu.data.repository.MessagingRepository
import com.example.thoodhu.data.repository.SettingsRepository
import com.example.thoodhu.ui.navigation.ThoodhuNavGraph
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.ThoodhuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = ThoodhuDatabase.getInstance(applicationContext)
        val authRepository = AuthRepository(
            userDao = database.userDao(),
            conversationDao = database.conversationDao(),
            messageDao = database.messageDao()
        )
        val messagingRepository = MessagingRepository(
            conversationDao = database.conversationDao(),
            messageDao = database.messageDao()
        )
        val settingsRepository = SettingsRepository(
            settingsDao = database.settingsDao()
        )

        setContent {
            val settings by settingsRepository.settings.collectAsStateWithLifecycle(initialValue = null)
            val isDark = when (settings?.themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            ThoodhuTheme(darkTheme = isDark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MidnightNavy
                ) {
                    val navController = rememberNavController()
                    ThoodhuNavGraph(
                        navController = navController,
                        authRepository = authRepository,
                        messagingRepository = messagingRepository,
                        settingsRepository = settingsRepository
                    )
                }
            }
        }
    }
}
