package com.example.visuallyimpared.screen

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.visuallyimpared.analyzer.ScheduleImageAnalyzer
import com.example.visuallyimpared.data.ocr.DayType
import com.example.visuallyimpared.data.ocr.Timetable
import com.example.visuallyimpared.ui.components.AppButton
import com.example.visuallyimpared.utils.rememberPhotoPicker
import kotlinx.coroutines.launch

@Composable
fun ImageRecognitionScreen(
    imageUri: Uri? = null,
    onRestart: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // If no URI is passed from navigation, we can still pick one locally
    val context = LocalContext.current
    var recognizedText by remember { mutableStateOf("") }
    val selectedImageUri = remember { mutableStateOf<Uri?>(imageUri) }
    val isInspectionMode = LocalInspectionMode.current

    val pickPhoto = rememberPhotoPicker { uri ->
        selectedImageUri.value = uri
    }
    
    val scope = rememberCoroutineScope()
    val analyzer = remember { ScheduleImageAnalyzer() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        // Image Container
        Surface(
            modifier = Modifier
                .weight(1.2f)
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White.copy(alpha = 0.95f), // High contrast off-white
//            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary), // Yellow border
            tonalElevation = 4.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri.value != null) {
                    AsyncImage(
                        model = selectedImageUri.value,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Text(
                        text = "No image selected",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.DarkGray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White.copy(alpha = 0.95f),
//            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            tonalElevation = 4.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = recognizedText.ifEmpty { "Recognized text will appear here..." },
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
            }
        }

        // Buttons at the bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppButton(
                onClick = {
                    onRestart()
                    recognizedText = ""
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Restart")
            }

            AppButton(
                onClick = {
                    selectedImageUri.value?.let { uri ->
                        scope.launch {
                            val bitmap = loadBitmapFromUri(context, uri)
                            if (bitmap != null) {
                                try {
                                    val timetables = analyzer.analyze(bitmap, context)
                                    recognizedText = formatTimetables(timetables)
                                } catch (e: Exception) {
                                    recognizedText = "Error: ${e.message}"
                                }
                            } else {
                                recognizedText = "Error: Could not load image"
                            }
                        }
                    }
                },
                enabled = selectedImageUri.value != null,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Recognize")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImageRecognitionScreenPreview() {
    ImageRecognitionScreen()
}

private fun loadBitmapFromUri(context: android.content.Context, uri: Uri): Bitmap? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }.copy(Bitmap.Config.ARGB_8888, true)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun formatTimetables(timetables: List<Timetable>): String {
    if (timetables.isEmpty()) return "No timetable data found."
    return timetables.joinToString("\n\n") { table ->
        val dayName = when (table.dayType) {
            DayType.WORKDAYS -> "Workdays"
            DayType.SATURDAY -> "Saturday"
            DayType.SUNDAY_HOLIDAY -> "Sunday & Holidays"
            else -> "Unknown Day"
        }
        "$dayName:\n" + table.departures.joinToString("\n") {
            "${it.hour}: ${it.minutes.joinToString(", ")}"
        }
    }
}
