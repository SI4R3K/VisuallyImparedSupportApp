package com.example.visuallyimpared.screen

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.visuallyimpared.ui.theme.VisuallyImparedTheme

@Composable
fun StartScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, bottom = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedText("Witaj")

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Wybierz jak chcesz wgrać zdjęcie...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {}) {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = "Zrób zdjęcie",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = "Zrób zdjęcie")
                }

                Button(onClick = {}) {
                    Icon(
                        imageVector = Icons.Filled.Upload,
                        contentDescription = "Wgraj zdjęcie",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = "Wgraj zdjęcie")
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun AnimatedText(content: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 20f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    Text(
        text = content,
        modifier = Modifier
            .alpha(alpha)
            .offset(y = offsetY.dp),
        fontSize = 36.sp,
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center,
    )
}

@Preview(showBackground = true)
@Composable
fun StartViewPreview() {
    VisuallyImparedTheme {
        StartScreen()
    }
}