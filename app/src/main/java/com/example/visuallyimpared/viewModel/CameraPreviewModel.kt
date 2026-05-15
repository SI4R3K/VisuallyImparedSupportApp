package com.example.visuallyimpared.viewModel

import androidx.camera.core.Camera
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.SurfaceRequest
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class CameraPreviewModel : ViewModel() {

    // SurfaceRequest state for preview
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest

    fun updateSurfaceRequest(request: SurfaceRequest) {
        _surfaceRequest.update { request }
    }

    // Camera reference
    private var camera: Camera? = null

    fun setCamera(newCamera: Camera) {
        this.camera = newCamera
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


}
