package com.example.visuallyimpared.viewModel

import android.content.Context
import android.net.Uri
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.visuallyimpared.utils.CameraFileUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class CameraPreviewModel : ViewModel() {

    // SurfaceRequest state for preview
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest

    private val executor = Executors.newSingleThreadExecutor()

    val preview = Preview.Builder().build().also {
        it.setSurfaceProvider { request ->
            _surfaceRequest.update { request }
        }
    }

    val imageCapture = ImageCapture.Builder().build()

    // Camera reference
    private var camera: Camera? = null

    fun bindCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner
    ) {
        viewModelScope.launch {
            val cameraProvider = ProcessCameraProvider.awaitInstance(context)
            cameraProvider.unbindAll()

            camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                preview,
                imageCapture
            )
        }
    }

    // Focus logic
    fun focusAt(
        offset: Offset,
        factory: SurfaceOrientedMeteringPointFactory
    ) {
        val camera = this.camera ?: return

        val point = factory.createPoint(offset.x, offset.y)

        val action = FocusMeteringAction.Builder(point).build()
        camera.cameraControl.startFocusAndMetering(action)
    }

    fun capturePhoto(
        context: Context,
        onImageCaptured: (Uri) -> Unit,
        onError: (ImageCaptureException) -> Unit
    ) {
        CameraFileUtils.takePicture(
            imageCapture = imageCapture,
            context = context,
            executor = executor,
            onImageCaptured = onImageCaptured,
            onError = onError
        )
    }

    override fun onCleared() {
        super.onCleared()
        executor.shutdown()
    }

}
