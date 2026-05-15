package com.example.visuallyimpared.screen

import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.visuallyimpared.analyzer.ScheduleImageAnalyzer
import com.example.visuallyimpared.utils.rememberPhotoPicker

@Composable
fun ImageRecognitionScreen(imageUri: Uri? = null) {
    // If no URI is passed from navigation, we can still pick one locally
    val context = LocalContext.current
    var recognizedText: String = remember { mutableStateOf("").toString() }
    val selectedImageUri = remember { mutableStateOf<Uri?>(imageUri) }

    val pickPhoto = rememberPhotoPicker { uri ->
        selectedImageUri.value = uri
    }

    val analyzer = remember {
        ScheduleImageAnalyzer(context) { text ->
            recognizedText = text
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Image Container - fills remaining space
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(5.dp),
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        // Recognized text container
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(5.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Recognized text will appear here... \n$recognizedText",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Buttons at the bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { pickPhoto() },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Upload image")
            }

            Button(
                onClick = {
                    selectedImageUri.value?.let { uri ->
                        analyzer.analyze(uri)
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
