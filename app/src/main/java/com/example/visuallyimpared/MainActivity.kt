package com.example.visuallyimpared

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.visuallyimpared.ui.theme.VisuallyImparedTheme
import com.example.visuallyimpared.screen.StartScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VisuallyImparedTheme {
                StartScreen()
//                val viewModel = remember { CameraPreviewModel() }
//                CameraPreviewScreen(viewModel)
            }
        }
    }
}
