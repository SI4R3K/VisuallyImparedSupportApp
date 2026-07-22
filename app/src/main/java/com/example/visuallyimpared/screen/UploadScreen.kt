package com.example.visuallyimpared.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.visuallyimpared.R
import com.example.visuallyimpared.ui.components.AppButton
import com.example.visuallyimpared.viewModel.ImageRecognitionViewModel

@Composable
fun UploadScreen(
    modifier: Modifier = Modifier,
    viewModel: ImageRecognitionViewModel,
    onRedo: () -> Unit = {},
    onConfirm: (stopId: String?) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val currentOnConfirm by rememberUpdatedState(onConfirm)

    // Automatically trigger recognition when image is set
    LaunchedEffect(uiState.selectedImageUri) {
        if (uiState.selectedImageUri != null && !uiState.isLoading && uiState.stopId == null && uiState.errorMessage == null) {
            viewModel.recognizeImage(context) { stopId ->
                currentOnConfirm(stopId)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background.copy(alpha = 0.90f)
            )
            .pointerInput(Unit) {
                detectTapGestures { }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Rozpoznawanie przystanku...",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else {
                AsyncImage(
                    model = uiState.selectedImageUri,
                    contentDescription = "Captured Image",
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, Color.White, RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Fit
                )

                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AppButton(
                        onClick = {
                            viewModel.reset()
                            onRedo()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(dimensionResource(R.dimen.padding_medium)),
                    ) {
                        Text("Ponów")
                    }

                    // Confirm button removed as it's now automatic
                }
            }
        }
    }
}
//@ComposePreview(showBackground = true)
//@Composable
//fun UploadScreenPreview() {
//    VisuallyImparedTheme {
//        UploadScreen(
//            capturedImageUri = Uri.EMPTY,
//            onRedo = {},
//            onConfirm = {}
//        )
//    }
//}