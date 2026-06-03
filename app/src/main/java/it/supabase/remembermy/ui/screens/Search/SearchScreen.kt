package it.supabase.remembermy.ui.screens.Search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.progettomobile.composable.BottomAppBar
import it.supabase.remembermy.composable.PostCard
import it.supabase.remembermy.composable.TopAppBar
import it.supabase.remembermy.ui.screens.Home.HomeViewModel
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
@Composable
fun SearchScreen(navController : NavHostController, viewModel: SearchViewModel){
    val state by viewModel.state.collectAsState()
    var searchText by remember { mutableStateOf("") }
    Scaffold(
        topBar = { TopAppBar("Cerca", navController) },
        bottomBar = { BottomAppBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Cerca eventi") },
                modifier = Modifier.fillMaxWidth()
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(state.posts) { post ->
                    PostCard(
                        post = post,
                        isFollowed = post.idEvent in state.followedEvents,
                        onFollowClick = {
                            viewModel.toggleFollow(post.idEvent)
                        }
                    )
                }
            }
        }
    }
}