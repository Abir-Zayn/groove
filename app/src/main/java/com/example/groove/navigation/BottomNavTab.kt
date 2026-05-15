package com.example.groove.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavTab(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    data object Home : BottomNavTab("home", "Home", Icons.Default.Home)
    data object History : BottomNavTab("history", "History", Icons.AutoMirrored.Filled.List)
    data object Settings : BottomNavTab("settings", "Settings", Icons.Default.Settings)
    data object Profile : BottomNavTab("profile", "Profile", Icons.Default.Person)

    companion object {
        val all: List<BottomNavTab> by lazy { listOf(Home, History, Settings, Profile) }
    }
}
