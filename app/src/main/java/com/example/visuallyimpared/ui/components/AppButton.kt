package com.example.visuallyimpared.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.visuallyimpared.ui.theme.AppButtonYellow

/**
 * The standard "go-to" button for the application.
 * Uses the primary theme color (Yellow) and onPrimary color (Black).
 */
@Composable
fun AppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        // These are defaults for Material3 Button when primary is yellow, 
        // but explicitly setting them ensures consistency.
        colors = ButtonDefaults.buttonColors(
            containerColor = AppButtonYellow,
            contentColor = Color.Black,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        ),
        content = content
    )
}

/**
 * A secondary button style for less prominent actions.
 * Usually outlined or a different color (like the AppButtonBlue).
 */
@Composable
fun AppSecondaryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp
        ),
        content = content
    )
}

@Preview(showBackground = false)
@Composable
fun AppButtonPreview() {
    AppSecondaryButton(onClick = {}) {
        Text("Example Button")
    }
}