package com.example.camerax

import androidx.camera.core.CameraSelector
import androidx.camera.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner

@Composable
fun CameraX_Screen (){

    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    val preview = Preview.Builder().build()
    val imageCapture = remember { ImageCapture.Builder().build() }



}