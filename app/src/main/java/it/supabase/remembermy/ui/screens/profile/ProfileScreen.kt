package it.supabase.remembermy.ui.screens.profile

import android.net.Uri
import android.widget.Space
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerDefaults.flingBehavior
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.progettomobile.composable.BottomAppBar
import it.supabase.remembermy.R
import it.supabase.remembermy.composable.LoadingImage
import it.supabase.remembermy.composable.Post
import it.supabase.remembermy.composable.PostCard
import it.supabase.remembermy.composable.TopAppBar
import it.supabase.remembermy.data.supabase.Profiles

@Composable
fun ToProfile(navController : NavHostController, profileModel : ProfileViewModel){
    val state by profileModel.state.collectAsState()
    val user = state.info

    // Initialize state variables that Compose can track
    var nickname by remember(user) { mutableStateOf(user?.nickname ?: "") }
    var email by remember(user) { mutableStateOf(user?.email ?: "") }
    var birthDate by remember(user) { mutableStateOf(user?.birth_date ?: "") }
    var level by remember(user) { mutableStateOf(user?.level?.toString() ?: "1.0") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        selectedImageUri = it
    }
    Scaffold(
        topBar = { TopAppBar("Profile", navController) },
        bottomBar = { BottomAppBar(navController) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding->

        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            /*Private Info columns and edit*/
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {

            }
            Spacer(Modifier.height(16.dp))

            if(!profileModel.state.collectAsState().value.events.isEmpty()){
                val posts = profileModel.state.collectAsState().value.events
                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    state = rememberLazyListState()
                ) {
                    items(posts){post->
                        val shownPost = Post(
                            username = user?.nickname.orEmpty(),
                            userImage = user?.avatar_url.orEmpty(),
                            postImage = "",
                            likes = 0,
                            description = post?.name_event.orEmpty()
                        )
                        PostCard(shownPost)

                    }
                }
            }else{
                Image(
                    painter = painterResource(R.drawable.camera_svgrepo_com),
                    contentDescription = "Camera Icon"
                )
            }

            /*Personal Events List*/
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (user != null) {
                                profileModel.actions.editProfile(
                                    Profiles(
                                        id_user = user.id_user,
                                        level = level.toFloatOrNull() ?: 1f,
                                        email = email,
                                        nickname = nickname,
                                        birth_date = birthDate,
                                        avatar_url = user.avatar_url,
                                        created_at = user.created_at
                                    )
                                )
                            }
                        }
                    ){
                        Text("Edit Profile")
                    }
                    Button(
                        onClick = {
                            profileModel.actions.logout()
                        }
                    ) {
                        Text("Logout")
                    }
                }
            }
        }

    }
}