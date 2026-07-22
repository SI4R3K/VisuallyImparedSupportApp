package com.example.visuallyimpared.screen

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.visuallyimpared.ui.components.AppButton
import com.example.visuallyimpared.viewModel.ImageRecognitionViewModel

@Composable
fun ImageRecognitionScreen(
    modifier: Modifier = Modifier,
    viewModel: ImageRecognitionViewModel,
    onRestart: () -> Unit = {},
    onRecognize: (stopId: String?) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        // Image container
        Surface(
            modifier = Modifier
                .weight(1.2f)
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White.copy(alpha = 0.95f), // High contrast off-white
            tonalElevation = 4.dp
        ) {
            Box(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // IMAGE
                if (uiState.selectedImageUri != null) {
                    AsyncImage(
                        model = uiState.selectedImageUri,
                        contentDescription = "Wybrana zdjęcie",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        // BUTTONS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
                AppButton(onClick = {
                    viewModel.reset()
                    onRestart()
                },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Restart")
                }

                AppButton(
                    onClick = {
                        viewModel.recognizeImage(context) { stopId ->
                            onRecognize(stopId)
                        }
                    },
                    enabled = uiState.selectedImageUri != null && !uiState.isLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Rozpoznaj")
                }
            }
    }
}

