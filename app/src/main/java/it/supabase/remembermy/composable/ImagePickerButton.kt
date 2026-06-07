package it.supabase.remembermy.composable

import android.Manifest
import android.R.attr.data
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import it.supabase.remembermy.utils.rememberMultiplePermissions
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun ImagePickerButton(
    onImageSelected: (Uri) -> Unit
) {
    val context = LocalContext.current
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }
    var showChoice by remember { mutableStateOf(false) }
    var pendingCamera by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onImageSelected(it) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) tempCameraUri?.let { onImageSelected(it) }
    }

    val permissionHandler = rememberMultiplePermissions(
        permissions = listOf(Manifest.permission.CAMERA),
        onResult = { statuses ->
            Log.d("CAMERA", "Permission result: $statuses")
            Log.d("CAMERA", "pendingCamera: $pendingCamera")
            if (statuses[Manifest.permission.CAMERA]?.isGranted == true && pendingCamera) {
                pendingCamera = false
                tempCameraUri = createImageUri(context)
                tempCameraUri?.let { cameraLauncher.launch(it) }
            } else if (statuses[Manifest.permission.CAMERA]?.isDenied == true) {
                pendingCamera = false
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Camera Permission Permanently Denied",
                        actionLabel = "Settings",
                        duration = SnackbarDuration.Long
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                }
            }
        }
    )

    if (showChoice) {
        AlertDialog(
            onDismissRequest = { showChoice = false },
            title = { Text("Scegli immagine") },
            text = {
                Column {
                    TextButton(onClick = {
                        showChoice = false
                        galleryLauncher.launch("image/*")
                    }) {
                        Icon(Icons.Default.Image, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Galleria")
                    }
                    TextButton(onClick = {
                        if (permissionHandler.statuses[Manifest.permission.CAMERA]?.isGranted == true) {
                            tempCameraUri = createImageUri(context)
                            tempCameraUri?.let { cameraLauncher.launch(it) }
                        } else {
                            pendingCamera = true
                            permissionHandler.launcherPermissionRequest()
                        }
                        showChoice = false
                    }) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Fotocamera")
                    }
                }
            },
            confirmButton = {}
        )
    }

    Button(onClick = { showChoice = true }) {
        Text("Aggiungi Foto")
    }
    SnackbarHost(hostState = snackbarHostState)
}
fun createImageUri(context: Context): Uri {
    val file = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
}