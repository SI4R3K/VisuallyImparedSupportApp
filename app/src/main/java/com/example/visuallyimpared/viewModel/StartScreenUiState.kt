package com.example.visuallyimpared.viewModel

import android.net.Uri

data class StartScreenUiState(
    val selectedImageUri: Uri? = null,
    val startState: Boolean = true,
    )
