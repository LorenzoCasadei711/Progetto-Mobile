package com.example.progettomobile.ui.screens.camera

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
class CameraViewModel : ViewModel() {
    var pictureUri by mutableStateOf<Uri?>(null)
        private set

    fun onPictureTaken(uri: Uri){
        pictureUri = uri
    }
}