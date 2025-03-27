package com.example.camerax

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import androidx.camera.view.PreviewView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Composable
fun CameraX_Screen() {
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current
    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }

    LaunchedEffect(cameraSelector) {  // Change to cameraSelector
        val cameraProvider = context.getCameraProvider()
        try {
            cameraProvider.unbindAll() // Ensure all previous bindings are removed
            if (!cameraProvider.hasCamera(cameraSelector)) {
                Toast.makeText(context, "Selected camera is not available", Toast.LENGTH_SHORT).show()
                return@LaunchedEffect
            }
             cameraProvider.bindToLifecycle(
                lifeCycleOwner, cameraSelector, preview, imageCapture
            )
            preview.setSurfaceProvider(previewView.surfaceProvider)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

        Row {
            Button(
                onClick = { capturePhoto(imageCapture, context) }
            ) {
                Text("Take Picture")
            }

            Button(
                onClick = {
                    cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    } else {
                        CameraSelector.DEFAULT_BACK_CAMERA
                    }
                }
            ) {
                Text("Switch Camera")
            }
        }
    }
}



//@Composable
//fun CameraX_Screen (){
//
//    val context = LocalContext.current
//    val lifeCycleOwner = LocalLifecycleOwner.current
//   // val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//    var cameraSelector by  remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
//    val preview = Preview.Builder().build()
//    val previewView = remember { PreviewView(context) } // Initialize PreviewView
//    val imageCapture = remember { ImageCapture.Builder().build() }
//
//
//    LaunchedEffect(Unit) {
//        val cameraProvider = context.getCameraProvider()
//        try{
//            cameraProvider.unbind()
//            cameraProvider.bindToLifecycle(
//                lifeCycleOwner  , cameraSelector , preview , imageCapture
//            )
//           // preview.surfaceProvider = previewView.surfaceProvider
//            preview.setSurfaceProvider(previewView.surfaceProvider)
//        }
//        catch (e:Exception){
//              e.printStackTrace()
//        }
//    }
//
//    Box(modifier = Modifier.fillMaxSize() , contentAlignment = Alignment.Center){
//        AndroidView(factory = { previewView }, modifier  = Modifier.fillMaxSize())
//
//        Row (){
//            Button(
//                onClick = {
//                    capturePhoto(imageCapture , context)
//                }
//            ) {
//                Text("Take Picture")
//            }
//
//            Button(
//                onClick = {
//                    cameraSelector = if(cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA){
//                        CameraSelector.DEFAULT_FRONT_CAMERA
//                    }
//                    else{
//                        CameraSelector.DEFAULT_BACK_CAMERA
//                    }
//                }
//            ) {
//                Text("Second Picture")
//            }
//        }
//    }
//
//
//}

private suspend fun Context.getCameraProvider():ProcessCameraProvider = suspendCoroutine {
    val cameraProviderFeature = ProcessCameraProvider.getInstance(this)
    cameraProviderFeature.addListener({
        it.resume(cameraProviderFeature.get())
    }, ContextCompat.getMainExecutor(this
    ))
}
private  fun capturePhoto(imageCapture : ImageCapture , context: Context){
    val name = "myCamera_${System.currentTimeMillis()}.jpg"
    val contentValue = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME , name)
        put(MediaStore.MediaColumns.MIME_TYPE , "image/jpeg")
        put(MediaStore.MediaColumns.RELATIVE_PATH , "Pictures/camerax-photos")
    }
    val outPut = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI , contentValue
    ).build()

    imageCapture.takePicture(
        outPut ,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback{
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
              Toast.makeText(context , "Photo Saved" , Toast.LENGTH_SHORT).show()
            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(context , "Photo Not Saved" , Toast.LENGTH_SHORT).show()
            }

        })
}