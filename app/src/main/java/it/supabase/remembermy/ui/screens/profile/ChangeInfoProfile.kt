package it.supabase.remembermy.ui.screens.profile

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.progettomobile.composable.BottomAppBar
import it.supabase.remembermy.R
import it.supabase.remembermy.composable.TopAppBar
import it.supabase.remembermy.data.supabase.Profiles

@Composable
fun ChangeInfoProfileScreen(navController : NavHostController, profileModel : ProfileViewModel){
    val state by profileModel.state.collectAsState()
    val user = state.info

    var nickname by remember(user) { mutableStateOf(user?.nickname ?: "") }
    var birthDate by remember(user) { mutableStateOf(user?.birth_date ?: "") }

    // This handles what Coil displays visually on screen
    var imageDisplay by remember(user) {
        mutableStateOf<Any>(
            user?.avatar_url?.ifBlank { null }?.toUri() ?: R.drawable.profile_simple_svgrepo_com
        )
    }

    // This specifically tracks the raw local Uri for uploading
    var selectedLocalUri by remember { mutableStateOf<Uri?>(null) }

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            imageDisplay = uri    // Updates Coil preview instantly
            selectedLocalUri = uri // Saves reference for the upload block
            Log.d("PhotoPicker", "Selected URI: $uri")
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    Scaffold(
        topBar = { TopAppBar("Cambio Info", navController)},
        bottomBar = { BottomAppBar(navController)},
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(
                painter = rememberAsyncImagePainter(imageDisplay),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(64.dp)
            )

            Button(
                onClick = {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            ) {
                Text("Change Profile Icon")
            }

            OutlinedTextField(
                value = nickname,
                onValueChange = {nickname = it},
                label = {Text("Nickname")}
            )

            OutlinedTextField(
                value = birthDate,
                onValueChange = {birthDate = it},
                label = {Text("Data di Nascita")}
            )

            Button(
                onClick = {
                    profileModel.actions.editProfile(
                        Profiles(
                            nickname = nickname,
                            birth_date = birthDate,
                            avatar_url = user?.avatar_url ?: "",
                            id_user = user?.id_user ?: "",
                            level = user?.level ?: 1f,
                            email = user?.email ?: "",
                            created_at = user?.created_at ?: "",
                        ),
                         selectedLocalUri
                    )
                }
            ) {
                Text("Cambia Info")
            }
        }
    }
}