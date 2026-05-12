package com.example.visuallyimpared.utils

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable


@Composable
fun rememberPhotoPicker(onPhotoPicked: (Uri?) -> Unit): () -> Unit {

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> onPhotoPicked(uri) }
    )

    return {
        launcher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }
}