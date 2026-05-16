package com.example.groove.view.screens.summarize

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groove.view.theme.GrooveSageGreen

@Composable
fun FileChip(
    fileName: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = GrooveSageGreen.copy(alpha = 0.12f),
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
        ) {
            Text(
                text = fileName,
                fontSize = 13.sp,
                color = GrooveSageGreen,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(28.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Remove file",
                    tint = GrooveSageGreen,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
fun ContextSlider(
    wordCount: Float,
    onWordCountChange: (Float) -> Unit,
    onInvalidInput: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDialog by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Summary Length") },
            text = {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it.filter { c -> c.isDigit() } },
                    label = { Text("Words (150 – 1000)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val num = inputText.toIntOrNull()
                    showDialog = false
                    when {
                        num == null -> Unit
                        num < 150 || num > 1000 -> onInvalidInput()
                        else -> onWordCountChange(num.toFloat())
                    }
                }) { Text("Set") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            },
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Summary Length",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            )
            Surface(
                onClick = {
                    inputText = wordCount.toInt().toString()
                    showDialog = true
                },
                shape = MaterialTheme.shapes.small,
                color = GrooveSageGreen.copy(alpha = 0.12f),
            ) {
                Text(
                    text = "${wordCount.toInt()} words",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GrooveSageGreen,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
        }
        Slider(
            value = wordCount,
            onValueChange = onWordCountChange,
            valueRange = 150f..1000f,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = GrooveSageGreen,
                activeTrackColor = GrooveSageGreen,
                inactiveTrackColor = GrooveSageGreen.copy(alpha = 0.2f),
            ),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "150",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            )
            Text(
                text = "1000",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            )
        }
    }
}

private fun AnnotatedString.Builder.appendInlineMarkdown(text: String) {
    val stripped = text.replace(Regex("<[^>]+>"), "")
    var i = 0
    while (i < stripped.length) {
        when {
            i + 1 < stripped.length && stripped[i] == '*' && stripped[i + 1] == '*' -> {
                val end = stripped.indexOf("**", i + 2)
                if (end != -1) {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(stripped.substring(i + 2, end))
                    }
                    i = end + 2
                } else {
                    append(stripped[i])
                    i++
                }
            }
            stripped[i] == '*' || stripped[i] == '_' -> {
                val marker = stripped[i]
                val end = stripped.indexOf(marker, i + 1)
                if (end != -1) {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(stripped.substring(i + 1, end))
                    }
                    i = end + 1
                } else {
                    append(stripped[i])
                    i++
                }
            }
            else -> {
                append(stripped[i])
                i++
            }
        }
    }
}

fun buildMarkdownAnnotatedString(text: String): AnnotatedString = buildAnnotatedString {
    val lines = text.lines()
    lines.forEachIndexed { index, line ->
        when {
            line.startsWith("### ") -> withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp)) {
                appendInlineMarkdown(line.removePrefix("### "))
            }
            line.startsWith("## ") -> withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 17.sp)) {
                appendInlineMarkdown(line.removePrefix("## "))
            }
            line.startsWith("# ") -> withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 19.sp)) {
                appendInlineMarkdown(line.removePrefix("# "))
            }
            line.startsWith("- ") || line.startsWith("* ") -> {
                append("• ")
                appendInlineMarkdown(line.substring(2))
            }
            line.startsWith("> ") -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                appendInlineMarkdown(line.removePrefix("> "))
            }
            line.trimStart().startsWith("---") || line.trimStart().startsWith("___") -> append("─────────────────")
            else -> appendInlineMarkdown(line)
        }
        if (index < lines.lastIndex) append("\n")
    }
}

@Composable
fun SummaryTextField(
    text: String,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    LaunchedEffect(text) {
        if (text.isNotEmpty()) scrollState.animateScrollTo(scrollState.maxValue)
    }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 220.dp, max = 420.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(
            width = 1.dp,
            color = if (text.isNotEmpty())
                GrooveSageGreen.copy(alpha = 0.35f)
            else
                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(16.dp),
        ) {
            if (text.isEmpty()) {
                Text(
                    text = "Summary will appear here...",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                )
            } else {
                Text(
                    text = buildMarkdownAnnotatedString(text),
                    fontSize = 15.sp,
                    lineHeight = 23.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

data class AiModel(val displayName: String, val modelId: String)

val availableModels = listOf(
    AiModel("DeepSeek V4 Flash", "deepseek/deepseek-v4-flash:free"),
    AiModel("Llama 3.3 70B", "meta-llama/llama-3.3-70b-instruct:free"),
    AiModel("Trinity Large", "arcee-ai/trinity-large-thinking:free"),
    AiModel("MiniMax M2.5", "minimax/minimax-m2.5:free"),
    AiModel("Gemma 4 26B", "google/gemma-4-26b-a4b-it:free"),
)

@Composable
fun ModelPicker(
    selectedModelId: String,
    onModelSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedModel = availableModels.find { it.modelId == selectedModelId } ?: availableModels[0]

    Box(modifier = modifier) {
        Surface(
            onClick = { expanded = true },
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = selectedModel.displayName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Select model",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            availableModels.forEach { model ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = model.displayName,
                            fontSize = 13.sp,
                            fontWeight = if (model.modelId == selectedModelId) FontWeight.SemiBold else FontWeight.Normal,
                        )
                    },
                    onClick = {
                        onModelSelected(model.modelId)
                        expanded = false
                    },
                    trailingIcon = if (model.modelId == selectedModelId) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = GrooveSageGreen,
                            )
                        }
                    } else null,
                )
            }
        }
    }
}

@Composable
fun StreamingButton(
    isStreaming: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isStreaming)
                MaterialTheme.colorScheme.error
            else
                GrooveSageGreen,
        ),
    ) {
        Icon(
            imageVector = if (isStreaming) Icons.Filled.Stop else Icons.Filled.PlayArrow,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = if (isStreaming) "Stop Streaming" else "Start Streaming",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
