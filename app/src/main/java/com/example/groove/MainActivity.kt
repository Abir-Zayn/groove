package com.example.groove

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.groove.feature.home.HomeScreen
import com.example.groove.feature.splash.SplashScreen
import com.example.groove.feature.summarize.SummarizeContent
import com.example.groove.ui.theme.GrooveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GrooveTheme {
                GrooveNavHost()
            }
        }
    }
}

@Composable
private fun GrooveNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(onGetStarted = { navController.navigate("home") })
        }
        composable("home") {
            HomeScreen(
                onNavigateToSummarize = { fileName ->
                    val encoded = Uri.encode(fileName)
                    navController.navigate("summarize?fileName=$encoded")
                }
            )
        }
        composable(
            route = "summarize?fileName={fileName}",
            arguments = listOf(
                navArgument("fileName") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            ),
        ) { backStackEntry ->
            val fileName = backStackEntry.arguments?.getString("fileName") ?: ""
            SummarizeContent(
                fileName = fileName,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
