package com.nathakusuma.neriva

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Modifier
import com.nathakusuma.neriva.data.local.ChatDataManager
import com.nathakusuma.neriva.data.local.TokenManager
import com.nathakusuma.neriva.data.local.UserDataManager
import com.nathakusuma.neriva.ui.navigation.AppNavigation
import com.nathakusuma.neriva.ui.theme.NerivaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize singleton managers with application context
        TokenManager.getInstance(applicationContext)
        UserDataManager.getInstance(applicationContext)
        ChatDataManager.getInstance(applicationContext)

        enableEdgeToEdge()
        setContent {
            NerivaTheme {
                // ensure all screens will be above the navigation bar
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
