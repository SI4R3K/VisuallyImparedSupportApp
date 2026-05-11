package com.example.visuallyimpared.ViewModel

import androidx.camera.core.SurfaceRequest
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class CameraPreviewModel : ViewModel() {
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest

    fun updateSurfaceRequest(request: SurfaceRequest) {
        _surfaceRequest.update { request }
    }
}
