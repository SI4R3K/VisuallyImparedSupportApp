package com.example.visuallyimpared

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.visuallyimpared.ui.theme.VisuallyImparedTheme
import android.util.Log
import org.opencv.android.OpenCVLoader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        if (OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "OpenCV initialization successful")
        } else {
            Log.e("OpenCV", "OpenCV initialization failed")
        }

        enableEdgeToEdge()
        setContent {
            VisuallyImparedTheme {
                VisuallyImparedApp()
            }
        }
    }
}



