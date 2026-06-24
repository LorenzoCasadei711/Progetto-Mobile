package it.supabase.remembermy.ui.screens.profile

import android.net.Uri
import android.text.Layout
import android.util.Log
import android.widget.Space
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerDefaults.flingBehavior
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.progettomobile.composable.BottomAppBar
import com.example.progettomobile.composable.NavigationRoute
import it.supabase.remembermy.R
import it.supabase.remembermy.composable.LoadingImage
import it.supabase.remembermy.composable.OpinionRow
import it.supabase.remembermy.composable.Post
import it.supabase.remembermy.composable.PostCard
import it.supabase.remembermy.composable.TopAppBar
import it.supabase.remembermy.data.supabase.Events
import it.supabase.remembermy.data.supabase.Profiles
import it.supabase.remembermy.ui.screens.Event.OpinionSection

@Composable
fun ToProfile(navController: NavHostController, profileModel: ProfileViewModel) {
    val state by profileModel.state.collectAsState()
    val user = state.info
    Log.d("PROFILE_DEBUG", user.toString())
    val posts = state.events
    val badges = state.badges

    var nickname by remember(user) { mutableStateOf(user?.nickname ?: "") }
    var email by remember(user) { mutableStateOf(user?.email ?: "") }
    var birthDate by remember(user) { mutableStateOf(user?.birth_date ?: "") }
    var level by remember(user) { mutableStateOf(user?.level?.toString() ?: "1.0") }
    var image by remember(user) {
        mutableStateOf(
            user?.avatar_url?.toUri() ?: R.drawable.profile_simple_svgrepo_com
        )
    }

    Scaffold(
        topBar = { TopAppBar("Profile", navController) },
        bottomBar = { BottomAppBar(navController) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        profileModel.actions.update()
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .weight(1f),
                state = rememberLazyListState()
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(image),
                                contentDescription = "Account Profile Image",
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape),
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Top
                        ) {
                            Text(
                                nickname,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Email: $email")
                            Text("Birth Date: $birthDate")
                            Text("Level: $level")
                        }
                    }
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        for (badge in badges){
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(2.dp)
                                    .background(
                                        color = Color.Yellow,
                                        RoundedCornerShape(25.dp)
                                    )
                                    .clip(RoundedCornerShape(25.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ){
                                Text(
                                    text = ("Level" + badge?.name_badge),
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(8.dp),
                                    color = Color.Black,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(thickness = 16.dp)
                }

                if (posts.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.camera_svgrepo_com),
                                contentDescription = "Camera Icon",
                                modifier = Modifier.size(128.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text("No Events Yet")
                        }
                    }
                } else {
                    items(posts) { post ->
                        ProfileCard(checkNotNull(post), navController, profileModel)
                        HorizontalDivider(
                            modifier = Modifier.height(2.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        profileModel.actions.logout()
                    }
                ) {
                    Text("Logout")
                }
                Spacer(Modifier.width(32.dp))

            }
        }

    }
}

@Composable
fun ProfileCard(event : Events, navController : NavHostController, profileViewModel: ProfileViewModel){

    val followNumber = event.followedEvents.size
    val opinions = event.opinions
    var myOpinion by remember {mutableStateOf("")}
    var deletionAlertShown by remember { mutableStateOf(false) }
    var isOpinionsVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            EventDeletionAlert(
                show = deletionAlertShown,
                name = event.name_event,
                onAction = {
                    profileViewModel.actions.deleteEvent(event)
                    profileViewModel.actions.update()
                           },
                onHide = { deletionAlertShown = false }
            )
            Text(
                text = event.name_event,
                modifier = Modifier.padding(horizontal = 12.dp),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = event.date_event,
                modifier = Modifier.padding(horizontal = 12.dp),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.width(10.dp))
        }
        AsyncImage(
            model = event.event_photo,
            contentDescription = "Foto del post",
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            contentScale = ContentScale.Crop,
            onError = {
                println("Errore caricamento immagine : ${it.result.throwable}")
            }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {navController.navigate(NavigationRoute.ChangeEvent(event))}
            ) {
                Text("Edit Event")
            }
            IconButton(onClick = {isOpinionsVisible = !isOpinionsVisible}) {
                Row {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comment icon"
                    )
                }
            }
            IconButton(
                onClick = {
                    deletionAlertShown = true
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Color.White,
                    containerColor = Color.Red
                )) {
                Row {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Comment icon"
                    )
                }
            }


        }

        Text(
            text = "$followNumber following this event",
            modifier = Modifier.padding(12.dp),
            fontWeight = FontWeight.Bold
        )

        Text(
            text = event.event_details?:"",
            modifier = Modifier.padding(12.dp),
            fontWeight = FontWeight.Medium
        )

        AnimatedVisibility(visible = isOpinionsVisible) {
            OpinionSection(event.id_event?:"", opinions, profileViewModel)
        }

    }
}


@Composable
fun EventDeletionAlert(
    show: Boolean,
    name : String,
    onAction: () -> Unit,
    onHide: () -> Unit
) {
    if (show) {
        AlertDialog(
            title = { Text("Sei Sicuro di voler eliminare questo evento?") },
            text = { Text(" $name") },
            confirmButton = {
                TextButton(onClick = {
                    onAction()
                    onHide()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onHide()
                }) {
                    Text("No")
                }
            },
            onDismissRequest = onHide
        )
    }
}