package com.example.progettomobile.composable

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

@Composable
fun rememberCameraLauncher(
    onPictureTaken: (Uri) ->Unit = {}
): Pair<Uri?,()->Unit>{
    var launcherUri by remember { mutableStateOf<Uri?>(null) }
    var pictureUri by remember { mutableStateOf<Uri?>(null) }

    val ctx = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {success ->
        if(success){
            launcherUri?.let{
                pictureUri = it
                onPictureTaken(it)
            }
        }
    }
    val takePicture = {
        val file = File.createTempFile(
            "tmp_image",
            ".jpg",
            ctx.externalCacheDir
        )
        launcherUri = FileProvider.getUriForFile(
            ctx,
            "${ctx.packageName}.provider",
            file
        )
        launcher.launch(launcherUri!!)
    }
    return pictureUri to takePicture
}