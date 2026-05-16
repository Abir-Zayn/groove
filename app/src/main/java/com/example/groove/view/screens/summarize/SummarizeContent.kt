package com.example.groove.view.screens.summarize

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import com.example.groove.controller.FileState
import com.example.groove.controller.SummarizeController
import com.example.groove.view.components.GrooveTopBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val MAX_CONTENT_CHARS = 12_000

@Composable
fun SummarizeContent(
    fileName: String,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val controller = remember { SummarizeController() }
    val snackbarHostState = remember { SnackbarHostState() }

    var wordCount by remember { mutableFloatStateOf(300f) }
    var isStreaming by remember { mutableStateOf(false) }
    var summaryText by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var streamJob by remember { mutableStateOf<Job?>(null) }

    fun stopStreaming() {
        streamJob?.cancel()
        isStreaming = false
    }

    fun startStreaming() {
        val uri = FileState.pendingUri ?: run {
            errorMsg = "No file attached"
            return
        }
        summaryText = ""
        errorMsg = null
        isStreaming = true
        streamJob = scope.launch {
            val content = withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver.openInputStream(uri)
                        ?.use { it.readBytes().toString(Charsets.UTF_8) }
                        ?: ""
                }.getOrNull()
            }
            if (content.isNullOrBlank()) {
                errorMsg = "Unable to read file content"
                isStreaming = false
                return@launch
            }
            controller.summarizeStreaming(content.take(MAX_CONTENT_CHARS), wordCount.toInt())
                .catch { errorMsg = it.message ?: "Streaming failed" }
                .collect { token -> summaryText += token }
            isStreaming = false
        }
    }

    Scaffold(
        topBar = {
            GrooveTopBar(
                title = "Groove Summarize",
                onBack = {
                    stopStreaming()
                    onBack()
                },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(20.dp))

            FileChip(
                fileName = fileName,
                onRemove = {
                    stopStreaming()
                    Log.d("GrooveNav", "File removed → $fileName")
                    FileState.pendingUri = null
                    onBack()
                },
            )

            Spacer(Modifier.height(24.dp))

            ContextSlider(
                wordCount = wordCount,
                onWordCountChange = { wordCount = it },
                onInvalidInput = {
                    scope.launch {
                        snackbarHostState.showSnackbar("Number should be within 150 to 1000")
                    }
                },
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ModelPicker(modifier = Modifier.weight(1f))
                StreamingButton(
                    isStreaming = isStreaming,
                    onClick = { if (isStreaming) stopStreaming() else startStreaming() },
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(16.dp))

            SummaryTextField(text = summaryText)

            if (errorMsg != null) {
                Spacer(Modifier.height(10.dp))
                Surface(
                    modifier = Modifier.padding(0.dp),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.errorContainer,
                ) {
                    Text(
                        text = errorMsg!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
