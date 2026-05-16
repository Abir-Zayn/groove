package com.example.groove.view.screens.home.components

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groove.controller.FileState
import com.example.groove.view.theme.GrooveSageGreen

private val ALLOWED_EXTENSIONS = setOf("pdf", "docx", "md", "txt")

@Composable
fun UploadFileContent(
    onError: (String) -> Unit,
    onFileUploaded: (fileName: String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var selectedFileName by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        val name = resolveFileName(context, uri)
        val ext = name.substringAfterLast('.', "").lowercase()
        if (ext in ALLOWED_EXTENSIONS) {
            FileState.pendingUri = uri
            selectedFileName = name
            onFileUploaded(name)
        } else {
            onError("Wrong File has been upload. Try with doc or txt file.")
        }
    }

    OutlinedCard(
        onClick = { launcher.launch(arrayOf("*/*")) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = if (selectedFileName != null) 2.dp else 1.5.dp,
            color = if (selectedFileName != null)
                GrooveSageGreen
            else
                GrooveSageGreen.copy(alpha = 0.4f),
        ),
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
                imageVector = if (selectedFileName != null)
                    Icons.Outlined.CheckCircle
                else
                    Icons.Outlined.CloudUpload,
                contentDescription = null,
                tint = GrooveSageGreen,
                modifier = Modifier.size(48.dp),
            )
            Text(
                text = selectedFileName ?: "Tap to upload document",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = if (selectedFileName != null)
                    GrooveSageGreen
                else
                    MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "Supported: PDF · DOCX · MD · TXT",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun resolveFileName(context: Context, uri: Uri): String {
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val col = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (col >= 0 && cursor.moveToFirst()) return cursor.getString(col)
    }
    return uri.lastPathSegment?.substringAfterLast('/') ?: ""
}
