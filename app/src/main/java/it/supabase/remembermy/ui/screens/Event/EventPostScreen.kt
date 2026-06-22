package it.supabase.remembermy.ui.screens.Event

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import it.supabase.remembermy.ui.screens.Search.SearchViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.progettomobile.composable.BottomAppBar
import it.supabase.remembermy.composable.PostCard

import it.supabase.remembermy.composable.TopAppBar
import it.supabase.remembermy.composable.rememberOSM

@Composable
fun EventPostScreen(
    navController : NavHostController,
    viewModel: SearchViewModel,
    idEvent: String
){
    val state by viewModel.state.collectAsState()
    val post = state.posts.find { it.idEvent == idEvent }

    val osm = rememberOSM()

    Scaffold(
        topBar = { TopAppBar("Evento",navController) },
        bottomBar = { BottomAppBar(navController) }
    ) {paddingValues ->
        if(post!=null){
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                PostCard(
                    post = post,
                    isFollowed = post.idEvent in state.followedEvents,
                    onFollowClick = {
                        viewModel.toggleFollow((post.idEvent))
                    },
                    navController = navController
                )
            }
        }
    }
}