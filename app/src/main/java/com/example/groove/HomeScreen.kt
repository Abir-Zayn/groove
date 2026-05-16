package com.example.groove

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groove.navigation.BottomNavTab
import com.example.groove.navigation.GrooveBottomNavBar
import com.example.groove.ui.theme.GrooveSageGreen
import com.example.groove.util.greeting
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(onNavigateToSummarize: (String) -> Unit = {}) {
    var selectedTab by remember { mutableStateOf<BottomNavTab>(BottomNavTab.Home) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        bottomBar = {
            GrooveBottomNavBar(
                tabs = BottomNavTab.all,
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                BottomNavTab.Home -> HomeContent(snackbarHostState, onNavigateToSummarize)
                BottomNavTab.History -> HistoryScreen()
                BottomNavTab.Settings -> SettingsScreen()
                BottomNavTab.Profile -> ProfileScreen()
            }
        }
    }
}

@Composable
private fun HomeContent(
    snackbarHostState: SnackbarHostState,
    onNavigateToSummarize: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = greeting("Abir Zayn"),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 24.dp, end = 20.dp),
        )
        Text(
            text = "What are you vibing to?",
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
            modifier = Modifier.padding(start = 20.dp, top = 4.dp),
        )
        ProUpgradeCard()

        Text(
            text = "Add your content",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 4.dp),
        )

        UploadFileContent(
            onError = { msg -> scope.launch { snackbarHostState.showSnackbar(msg) } },
            onFileUploaded = onNavigateToSummarize,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        )

        SecondActionContainer(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SecondActionContainer(modifier: Modifier = Modifier) {
    OutlinedCard(
        onClick = {},
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, GrooveSageGreen.copy(alpha = 0.25f)),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.MoreHoriz,
                contentDescription = null,
                tint = GrooveSageGreen.copy(alpha = 0.4f),
                modifier = Modifier.size(48.dp),
            )
            Text(
                text = "Coming soon",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                textAlign = TextAlign.Center,
            )
        }
    }
}
