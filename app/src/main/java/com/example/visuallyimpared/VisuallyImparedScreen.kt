package com.example.visuallyimpared

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.visuallyimpared.screen.CameraPreviewScreen
import com.example.visuallyimpared.screen.ImageRecognitionScreen
import com.example.visuallyimpared.screen.StartScreen
import com.example.visuallyimpared.viewModel.CameraPreviewModel

enum class VisuallyImparedScreen {
    Start,
    CameraPreview,
    ImageRecognition
}

@Composable
fun VisuallyImparedApp(
    navController: NavHostController = rememberNavController()
) {
    val cameraViewModel = remember { CameraPreviewModel() }

    NavHost(
        navController = navController,
        startDestination = VisuallyImparedScreen.Start.name
    ) {
        composable(route = VisuallyImparedScreen.Start.name) {
            StartScreen(
                onTakePhoto = {
                    navController.navigate(VisuallyImparedScreen.CameraPreview.name)
                },
                onConfirmUpload = { uri ->
                    navController.navigate("${VisuallyImparedScreen.ImageRecognition.name}/${Uri.encode(uri.toString())}")
                }
            )
        }

        composable(route = VisuallyImparedScreen.CameraPreview.name) {
            CameraPreviewScreen(
                viewModel = cameraViewModel,
                onImageCaptured = { uri ->
                    navController.navigate("${VisuallyImparedScreen.ImageRecognition.name}/${Uri.encode(uri.toString())}")
                }
            )
        }

        composable(
            route = "${VisuallyImparedScreen.ImageRecognition.name}/{imageUri}"
        ) { backStackEntry ->
            val imageUriStr = backStackEntry.arguments?.getString("imageUri")
            val imageUri = imageUriStr?.toUri()
            ImageRecognitionScreen(
                imageUri = imageUri,
                onRestart = {
                    cancelOrderAndNavigateToStart(navController)
                }
            )
        }
    }
}

private fun cancelOrderAndNavigateToStart(
    navController: NavHostController
) {
    navController.popBackStack(
        VisuallyImparedScreen.Start.name,
        inclusive = false
    )
}
