package com.example.visuallyimpared

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.visuallyimpared.ui.theme.VisuallyImparedTheme
import com.example.visuallyimpared.Screen.CameraPreviewScreen
import com.example.visuallyimpared.ViewModel.CameraPreviewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VisuallyImparedTheme {
                val viewModel = CameraPreviewModel()
                CameraPreviewScreen(viewModel)
            }
        }
    }
}
