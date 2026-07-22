package com.example.visuallyimpared

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.visuallyimpared.analyzer.ScheduleImageAnalyzer
import com.example.visuallyimpared.screen.CameraPreviewScreen
import com.example.visuallyimpared.screen.DepartureInfoScreen
import com.example.visuallyimpared.screen.ImageRecognitionScreen
import com.example.visuallyimpared.screen.StartScreen
import com.example.visuallyimpared.screen.UploadScreen
import com.example.visuallyimpared.viewModel.CameraPreviewModel
import com.example.visuallyimpared.viewModel.DepartureViewModel
import com.example.visuallyimpared.viewModel.ImageRecognitionViewModel

enum class VisuallyImparedScreen {
    Start,
    CameraPreview,
    Upload,
    DepartureInfo
}

@Composable
fun VisuallyImparedApp(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as VisuallyImparedApplication).gtfsRepository
    val tts = (context.applicationContext as VisuallyImparedApplication).ttsService

    val analyzer = remember {
        ScheduleImageAnalyzer()
    }

    val cameraViewModel = remember { CameraPreviewModel() }

    val uploadScreenViewModel = remember {
        ImageRecognitionViewModel(
            analyzer
        )
    }

    val departureViewModel = remember {
        DepartureViewModel(
            repository,
            tts
        )
    }


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
                    navController.navigate("${VisuallyImparedScreen.Upload.name}/${Uri.encode(uri.toString())}")
                }
            )
        }

        composable(route = VisuallyImparedScreen.CameraPreview.name) {
            CameraPreviewScreen(
                viewModel = cameraViewModel,
                onImageCaptured = { uri ->
                    navController.navigate("${VisuallyImparedScreen.Upload.name}/${Uri.encode(uri.toString())}")
                }
            )
        }

        composable(
            route = "${VisuallyImparedScreen.Upload.name}/{imageUri}",
        ) { backStackEntry ->
            val imageUriStr = backStackEntry.arguments?.getString("imageUri")
            val imageUri = imageUriStr?.toUri()
            
            LaunchedEffect(imageUri) {
                uploadScreenViewModel.setImage(imageUri)
            }
            
            UploadScreen(
                viewModel = uploadScreenViewModel,
                onRedo = {
                    cancelOrderAndNavigateToStart(navController)
                },
                onConfirm = { stopId ->
                    Log.d("OCR DEBUG", "Navigating with stopId = $stopId")
                    navController.navigate(
                        "${VisuallyImparedScreen.DepartureInfo.name}/${stopId}"
                    )
                }
            )
        }

        composable(
            route = "${VisuallyImparedScreen.DepartureInfo.name}/{stopId}",
        ) { backStackEntry ->
            val stopId = backStackEntry.arguments?.getString("stopId")
            Log.d("OCR DEBUG", "Route argument stopId = $stopId")
            
            LaunchedEffect(stopId) {
                departureViewModel.setStopCode(stopId ?: "")
            }

            DepartureInfoScreen(
                viewModel = departureViewModel,
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
