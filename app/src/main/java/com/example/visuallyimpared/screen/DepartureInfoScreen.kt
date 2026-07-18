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
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.visuallyimpared.viewModel.DepartureViewModel
import com.example.visuallyimpared.viewModel.ImageRecognitionViewModel

@Composable
fun DepartureInfoScreen(
    viewModel: DepartureViewModel,
    onRestart: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val speechRate by viewModel.speechRate.collectAsState()

    // Generate stop info once on mount
    LaunchedEffect(Unit) {
        viewModel.generateStopInfo()
    }

    // Auto-announce when data is loaded
    LaunchedEffect(uiState.stopInfo) {
        if (uiState.stopInfo != null) {
            viewModel.speakStopInfo()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Surface(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White.copy(alpha = 0.95f),
            tonalElevation = 4.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // RESULT
                when {
                    uiState.isLoading -> CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.semantics { contentDescription = "Przetwarzanie obrazu" }
                    )

                    uiState.errorMessage != null ->
                        Text(
                            uiState.errorMessage!!,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )

                    uiState.stopInfo != null ->
                        DepartureList(
                            stopInfo = uiState.stopInfo!!,
                            onRouteClick = { route ->
                                viewModel.speakRouteInfo(
                                    route.routeName,
                                    route.headSign,
                                    route.departureTimes
                                )
                            }
                        )

                    else ->
                        Text("Wyniki pojawią się po rozpoznaniu")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Speech Rate Slider
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Tempo mowy: ${"%.1f".format(speechRate)}x",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
            Slider(
                value = speechRate,
                onValueChange = { viewModel.updateSpeechRate(it) },
                valueRange = 0.5f..2.0f,
                steps = 5, // 0.5, 0.75, 1.0, 1.25, 1.5, 1.75, 2.0
                modifier = Modifier.fillMaxWidth()
            )
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
                viewModel.stopSpeaking()
                viewModel.reset()
                onRestart()
            },
                modifier = Modifier.weight(1f)
            ) {
                Text("Restart")
            }

            AppButton(
                onClick = {
                    viewModel.speakStopInfo()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (isSpeaking) "Stop" else "Głos")
            }
        }
    }
}

@Composable
fun DepartureList(
    stopInfo: StopInfo,
    onRouteClick: (com.example.visuallyimpared.data.dto.RouteInfo) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Text(
                text = "PRZYSTANEK: ${stopInfo.stopName}",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        if (stopInfo.routes.isEmpty())
        {
            item {
                Text(
                    text = "Nie znaleziono odjazdów dla rozpoznanych linii.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.DarkGray
                )
            }
        } else {
            items(stopInfo.routes)
            {
                route ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRouteClick(route) }
                        .padding(vertical = 8.dp)
                        .semantics(mergeDescendants = true)
                        {
                            contentDescription =
                                "Linia ${route.routeName}, odjazdy: ${route.departureTimes.joinToString(", ")}"
                        }
                ) {
                    Text(
                        text = "LINIA ${route.routeName}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )
                    Text(
                        text = "KIERUNEK: ${route.headSign}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Blue,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )
                    Text(
                        text = route.departureTimes.joinToString(", "),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )
                    HorizontalDivider(
                        color = Color.LightGray,
                        thickness = 1.dp
                    )
                }
            }
        }
    }
}