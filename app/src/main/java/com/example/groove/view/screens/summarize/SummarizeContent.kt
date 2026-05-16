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
import java.io.ByteArrayOutputStream
import java.io.BufferedInputStream
import java.io.InputStream
import java.util.zip.Inflater
import java.util.zip.ZipInputStream
import com.example.groove.controller.FileState
import com.example.groove.controller.SummarizeController
import com.example.groove.view.components.GrooveTopBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val MAX_CONTENT_CHARS = 12_000

private fun extractText(stream: InputStream, fileName: String): String {
    val lower = fileName.lowercase()
    return when {
        lower.endsWith(".docx") -> extractDocxText(stream)
        lower.endsWith(".pdf") -> extractPdfText(stream)
        else -> stream.readBytes().toString(Charsets.UTF_8)
    }
}

private fun extractPdfText(stream: InputStream): String {
    val pdf = stream.readBytes()
    val result = StringBuilder()
    val STREAM_KW = "stream".toByteArray()
    var pos = 0
    while (pos < pdf.size - 10) {
        val si = pdfIndexOf(pdf, STREAM_KW, pos) ?: break
        // Skip "endstream"
        if (si >= 3 && pdf[si-1] == 'd'.code.toByte() && pdf[si-2] == 'n'.code.toByte() && pdf[si-3] == 'e'.code.toByte()) {
            pos = si + STREAM_KW.size; continue
        }
        // Dictionary window: last 512 bytes before "stream"
        val winStart = maxOf(0, si - 512)
        val dict = String(pdf, winStart, si - winStart, Charsets.ISO_8859_1)
        val length = Regex("/Length\\s+(\\d+)").findAll(dict).lastOrNull()?.groupValues?.get(1)?.toIntOrNull()
        if (length == null || length <= 0) { pos = si + STREAM_KW.size; continue }
        val isFlate = dict.contains("FlateDecode")
        // Advance past "stream\r?\n"
        var dataStart = si + STREAM_KW.size
        if (dataStart < pdf.size && pdf[dataStart] == '\r'.code.toByte()) dataStart++
        if (dataStart < pdf.size && pdf[dataStart] == '\n'.code.toByte()) dataStart++
        if (dataStart + length > pdf.size) { pos = si + STREAM_KW.size; continue }
        val streamData = pdf.copyOfRange(dataStart, dataStart + length)
        val contentBytes = if (isFlate) pdfInflate(streamData) else streamData
        if (contentBytes != null) {
            val content = String(contentBytes, Charsets.ISO_8859_1)
            if ("BT" in content || "Tj" in content || "TJ" in content) {
                result.append(extractPdfContentText(content)).append('\n')
            }
        }
        pos = dataStart + length
    }
    return result.toString()
        .replace(Regex("[\\x00-\\x08\\x0b\\x0c\\x0e-\\x1f\\x7f]"), "")
        .replace(Regex("[ \\t]+"), " ")
        .replace(Regex("\\n{3,}"), "\n\n")
        .trim()
}

private fun pdfInflate(data: ByteArray): ByteArray? = try {
    val inf = Inflater()
    inf.setInput(data)
    val out = ByteArrayOutputStream(data.size * 3)
    val buf = ByteArray(8192)
    while (!inf.finished() && !inf.needsInput()) {
        val n = inf.inflate(buf)
        if (n > 0) out.write(buf, 0, n)
    }
    inf.end()
    out.toByteArray()
} catch (e: Exception) { null }

private fun extractPdfContentText(stream: String): String {
    val sb = StringBuilder()
    for (block in Regex("BT([\\s\\S]*?)ET").findAll(stream)) {
        for (m in Regex("\\(([^)\\\\]|\\\\.)*\\)").findAll(block.groupValues[1])) {
            sb.append(
                m.value.removeSurrounding("(", ")")
                    .replace("\\n", "\n").replace("\\r", "\r")
                    .replace("\\t", " ").replace("\\(", "(")
                    .replace("\\)", ")").replace("\\\\", "\\")
                    .filter { it >= ' ' || it == '\n' }
            )
        }
        sb.append('\n')
    }
    return sb.toString()
}

private fun pdfIndexOf(arr: ByteArray, seq: ByteArray, start: Int): Int? {
    outer@ for (i in start..arr.size - seq.size) {
        for (j in seq.indices) { if (arr[i + j] != seq[j]) continue@outer }
        return i
    }
    return null
}

private fun extractDocxText(stream: InputStream): String {
    val zip = ZipInputStream(BufferedInputStream(stream))
    var entry = zip.nextEntry
    while (entry != null) {
        if (entry.name == "word/document.xml") {
            val xml = zip.readBytes().toString(Charsets.UTF_8)
            return xml
                .replace(Regex("</w:p>"), "\n")
                .replace(Regex("<[^>]+>"), "")
                .replace(Regex("[ \t]+"), " ")
                .replace(Regex("\n{3,}"), "\n\n")
                .trim()
        }
        entry = zip.nextEntry
    }
    return ""
}

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
        Log.d("GrooveStream", "phase=CANCELLED — user stopped stream at ${summaryText.length} chars received")
        streamJob?.cancel()
        isStreaming = false
    }

    fun startStreaming() {
        val uri = FileState.pendingUri ?: run {
            Log.d("GrooveStream", "phase=ERROR — no file attached")
            errorMsg = "No file attached"
            return
        }
        Log.d("GrooveStream", "phase=INIT — file=$fileName targetWords=${wordCount.toInt()}")
        summaryText = ""
        errorMsg = null
        isStreaming = true
        streamJob = scope.launch {
            Log.d("GrooveStream", "phase=THINKING — reading file content from URI")
            val content = withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver.openInputStream(uri)
                        ?.use { stream -> extractText(stream, fileName) }
                        ?: ""
                }.getOrNull()
            }
            if (content.isNullOrBlank()) {
                Log.d("GrooveStream", "phase=ERROR — file content empty or unreadable")
                errorMsg = "Unable to read file content"
                isStreaming = false
                return@launch
            }
            val rawChars = content.length
            val truncated = content.take(MAX_CONTENT_CHARS)
            Log.d("GrooveStream", "phase=EXECUTING — content ready rawChars=$rawChars truncated=${rawChars > MAX_CONTENT_CHARS} sending to LLM")
            var tokenCount = 0
            controller.summarizeStreaming(truncated, wordCount.toInt())
                .catch {
                    Log.d("GrooveStream", "phase=ERROR — stream failed: ${it.message}")
                    errorMsg = it.message ?: "Streaming failed"
                }
                .collect { token ->
                    if (tokenCount == 0) Log.d("GrooveStream", "phase=ACCELERATING — first token received stream active")
                    tokenCount++
                    summaryText += token
                }
            Log.d("GrooveStream", "phase=COMPLETE — done totalTokens=$tokenCount totalChars=${summaryText.length}")
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
