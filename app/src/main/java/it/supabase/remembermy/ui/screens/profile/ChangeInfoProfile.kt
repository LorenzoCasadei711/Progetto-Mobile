package it.supabase.remembermy.ui.screens.profile

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.progettomobile.composable.BottomAppBar
import com.example.progettomobile.composable.NavigationRoute
import it.supabase.remembermy.R
import it.supabase.remembermy.composable.ImagePickerButton
import it.supabase.remembermy.composable.TopAppBar
import it.supabase.remembermy.data.supabase.Profiles

@Composable
fun ChangeInfoProfileScreen(navController : NavHostController, profileModel : ProfileViewModel){
    profileModel.actions.update("utente")
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

    var selectedLocalUri by remember { mutableStateOf<Uri?>(null) }

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            imageDisplay = uri    // Updates Coil preview instantly
            selectedLocalUri = uri
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
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(16.dp))
            Image(
                painter = rememberAsyncImagePainter(selectedLocalUri?:imageDisplay),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(128.dp)
                    .border(BorderStroke(3.dp, Color.Black)),
                contentScale = ContentScale.Crop,

            )

            ImagePickerButton { selectedLocalUri = it }
            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth(1f)
            ) {
                OutlinedTextField(
                    value = nickname,
                    onValueChange = {nickname = it},
                    label = {Text("Nickname")},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = {birthDate = it},
                    label = {Text("Data di Nascita")},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }


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
                    profileModel.actions.update("utente")
                    navController.navigateUp()
                }
            ) {
                Text("Cambia Info")
            }
        }
    }
}