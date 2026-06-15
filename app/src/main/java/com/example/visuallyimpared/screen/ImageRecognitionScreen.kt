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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.visuallyimpared.data.dto.StopInfo
import com.example.visuallyimpared.ui.components.AppButton
import com.example.visuallyimpared.utils.rememberPhotoPicker
import com.example.visuallyimpared.viewModel.ImageRecognitionViewModel

@Composable
fun ImageRecognitionScreen(
    viewModel: ImageRecognitionViewModel,
    onRestart: () -> Unit = {},
    onRecognize: (stopId: String?) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    val pickPhoto = rememberPhotoPicker { uri ->
        viewModel.setImage(uri)
    }

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
                        viewModel.recognizeImage()
                        onRecognize(uiState.stopId)
                              },
                    enabled = uiState.selectedImageUri != null && !uiState.isLoading,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Rozpoznaj")
                }
            }
    }
}

//        Spacer(modifier = Modifier.height(20.dp))
//
//        Surface(
//            modifier = Modifier
//                .weight(1f)
//                .padding(start = 16.dp, end = 16.dp)
//                .fillMaxWidth(),
//            shape = RoundedCornerShape(16.dp),
//            color = Color.White.copy(alpha = 0.95f),
//            tonalElevation = 4.dp
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                // RESULT
//                when {
//                    uiState.isLoading -> CircularProgressIndicator(
//                        color = MaterialTheme.colorScheme.primary,
//                        modifier = Modifier.semantics { contentDescription = "Przetwarzanie obrazu" }
//                    )
//
//                    uiState.errorMessage != null ->
//                        Text(
//                            uiState.errorMessage!!,
//                            style = MaterialTheme.typography.bodyLarge,
//                            color = Color.Red,
//                            fontWeight = FontWeight.Bold
//                        )
//
//                    uiState.stopInfo != null ->
//                        DepartureList(uiState.stopInfo!!)
//
//                    else ->
//                        Text("Wyniki pojawią się po rozpoznaniu")
//                }
//            }
//        }

