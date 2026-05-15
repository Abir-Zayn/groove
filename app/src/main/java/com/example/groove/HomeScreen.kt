package com.example.groove

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.groove.navigation.BottomNavTab
import com.example.groove.navigation.GrooveBottomNavBar

@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableStateOf<BottomNavTab>(BottomNavTab.Home) }

    Scaffold(
        bottomBar = {
            GrooveBottomNavBar(
                tabs = BottomNavTab.all,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                BottomNavTab.Home -> HomeContent()
                BottomNavTab.History -> HistoryScreen()
                BottomNavTab.Settings -> SettingsScreen()
                BottomNavTab.Profile -> ProfileScreen()
            }
        }
    }
}

@Composable
private fun HomeContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Hello World",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}
