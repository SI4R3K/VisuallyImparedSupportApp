package com.example.visuallyimpared

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.visuallyimpared.ui.theme.VisuallyImparedTheme
import com.example.visuallyimpared.screen.CameraPreviewScreen
import com.example.visuallyimpared.screen.StartScreen
import com.example.visuallyimpared.screen.StartViewPreview
import com.example.visuallyimpared.ViewModel.CameraPreviewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VisuallyImparedTheme {
//                StartScreen()
                val viewModel = remember { CameraPreviewModel() }
                CameraPreviewScreen(viewModel)
            }
        }
    }
}
