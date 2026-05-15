package com.example.visuallyimpared.screen

import android.net.Uri
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
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
import androidx.compose.ui.tooling.preview.Preview as ComposePreview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.visuallyimpared.viewModel.CameraPreviewModel
import com.example.visuallyimpared.ui.theme.VisuallyImparedTheme
import com.example.visuallyimpared.utils.CameraFileUtils.takePicture
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.delay
import java.util.UUID
import java.util.concurrent.Executors

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
        modifier = modifier.fillMaxSize().wrapContentSize().widthIn(max = 480.dp),
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
    val executor = remember { Executors.newSingleThreadExecutor() }
    val capturedImageUri = remember { mutableStateOf<Uri?>(null) }

    val preview = remember { Preview.Builder().build() }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    var camera by remember { mutableStateOf<Camera?>(null) }

    LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.awaitInstance(context)
        cameraProvider.unbindAll()
        camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
    }

    LaunchedEffect(preview) {
        preview.setSurfaceProvider { request ->
            viewModel.updateSurfaceRequest(request)
        }
    }

    val surfaceRequest by viewModel.surfaceRequest.collectAsStateWithLifecycle()
    val coordinateTransformer = remember { MutableCoordinateTransformer() }

    var autofocusRequest by remember { mutableStateOf(UUID.randomUUID() to Offset.Unspecified)}
    val autofocusRequestId = autofocusRequest.first
    val showAutofocusIndicator = autofocusRequest.second.isSpecified
    val autofocusCoords = remember(autofocusRequestId) { autofocusRequest.second }

    if (showAutofocusIndicator) {
        LaunchedEffect(autofocusRequestId) {
            delay(1000)
            autofocusRequest = autofocusRequestId to Offset.Unspecified
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        surfaceRequest?.let { request ->
            CameraXViewfinder(
                surfaceRequest = request,
                coordinateTransformer = coordinateTransformer,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { tapCoords ->
                            val transformedOffset = with(coordinateTransformer) {
                                tapCoords.transform()
                            }

                            val factory = SurfaceOrientedMeteringPointFactory(
                                request.resolution.width.toFloat(),
                                request.resolution.height.toFloat()
                            )
                            val point = factory.createPoint(transformedOffset.x, transformedOffset.y)
                            val action = FocusMeteringAction.Builder(point).build()
                            camera?.cameraControl?.startFocusAndMetering(action)

                            autofocusRequest = UUID.randomUUID() to tapCoords
                        }
                    }
            )
        }

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

        Button(
            onClick = {
                takePicture(imageCapture, context, executor, {uri ->
                    capturedImageUri.value = uri
                }, { _ ->
                    // Handle error
                })
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
            UploadScreen(capturedImageUri)
        }
    }
}