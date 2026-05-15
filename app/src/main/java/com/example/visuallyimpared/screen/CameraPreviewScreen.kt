package com.example.visuallyimpared.screen

import android.net.Uri
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.takeOrElse
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visuallyimpared.viewModel.CameraPreviewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.delay
import java.util.UUID

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPreviewScreen(
    viewModel: CameraPreviewModel,
    modifier: Modifier = Modifier
) {
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    if (cameraPermissionState.status.isGranted) {
        CameraPreviewContent(viewModel, modifier)
    } else {
        PermissionScreen(cameraPermissionState, modifier)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionScreen(
    cameraPermissionState: com.google.accompanist.permissions.PermissionState,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize()
            .widthIn(max = 480.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
            "Whoops! Looks like we need your camera to work our magic!" +
                    "Don't worry, we just wanna see your pretty face (and maybe some cats).  " +
                    "Grant us permission and let's get this party started!"
        } else {
            "Hi there! We need your camera to work our magic! ✨\n" +
                    "Grant us permission and let's get this party started! \uD83C\uDF89"
        }
        Text(textToShow, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
            Text("Unleash the Camera!")
        }
    }
}

@Composable
private fun CameraPreviewContent(
    viewModel: CameraPreviewModel,
    modifier: Modifier = Modifier,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val context = LocalContext.current
    val capturedImageUri = remember { mutableStateOf<Uri?>(null) }

    val surfaceRequest by viewModel.surfaceRequest.collectAsStateWithLifecycle()
    val coordinateTransformer = remember { MutableCoordinateTransformer() }

    var autofocusRequest by remember { mutableStateOf(UUID.randomUUID() to Offset.Unspecified)}
    val autofocusRequestId = autofocusRequest.first
    val showAutofocusIndicator = autofocusRequest.second.isSpecified
    val autofocusCoords = remember(autofocusRequestId) { autofocusRequest.second }

    // Camera binding
    LaunchedEffect(lifecycleOwner) {
        viewModel.bindCamera(context, lifecycleOwner)
    }

    if (showAutofocusIndicator) {
        LaunchedEffect(autofocusRequestId) {
            delay(1000)
            autofocusRequest = autofocusRequestId to Offset.Unspecified
        }
    }

    // Displaying the camera view from device
    Box(modifier = modifier.fillMaxSize()) {
        surfaceRequest?.let { request ->
            val factory = remember(request) {
                SurfaceOrientedMeteringPointFactory(
                    request.resolution.width.toFloat(),
                    request.resolution.height.toFloat()
                )
            }
            CameraXViewfinder(
                surfaceRequest = request,
                coordinateTransformer = coordinateTransformer,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { tapCoords ->
                            viewModel.focusAt(
                                tapCoords,
                                factory = factory
                            )
                            autofocusRequest = UUID.randomUUID() to tapCoords
                        }
                    }
            )
        }

        // White circle to indicate focusing action
        AnimatedVisibility(
            visible = showAutofocusIndicator,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .offset { autofocusCoords.takeOrElse { Offset.Zero }.round() }
                    .offset((-24).dp, (-24).dp)
                    .size(48.dp)
                    .border(2.dp, Color.White, CircleShape)
            )
        }

        // Take picture button
        Button(
            onClick = {
                viewModel.capturePhoto(
                    context = context,
                    onImageCaptured = { uri ->
                        capturedImageUri.value = uri
                    },
                    onError = { _ ->
                        // Handle error
                    }
                )
            },
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .size(80.dp)
                .border(4.dp, Color.White, CircleShape),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.8f)
            )
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(shape = CircleShape)
                    .background(Color.White)
            )
        }

        // Display the captured image in the center with confirmation options
        capturedImageUri.value?.let { uri ->
            UploadScreen(
                capturedImageUri = capturedImageUri,
                onRedo = {
                    capturedImageUri.value = null
                }
            )
        }
    }
}