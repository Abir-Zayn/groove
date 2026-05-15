package com.example.groove

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.groove.ui.theme.GrooveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GrooveTheme {
                var showHome by remember { mutableStateOf(false) }
                if (showHome) {
                    HomeScreen()
                } else {
                    SplashScreen(onGetStarted = { showHome = true })
                }
            }
        }
    }
}
