package com.example.groove.feature.summarize

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groove.ui.components.GrooveTopBar
import com.example.groove.ui.theme.GrooveSageGreen

@Composable
fun SummarizeContent(
    fileName: String,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            GrooveTopBar(
                title = "Groove Summarize",
                onBack = onBack,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(24.dp))

            Surface(
                shape = MaterialTheme.shapes.small,
                color = GrooveSageGreen.copy(alpha = 0.12f),
            ) {
                Text(
                    text = fileName,
                    fontSize = 13.sp,
                    color = GrooveSageGreen,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }
        }
    }
}
