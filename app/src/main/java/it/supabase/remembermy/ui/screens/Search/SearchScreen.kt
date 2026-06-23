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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.items
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.progettomobile.composable.NavigationRoute
import androidx.compose.foundation.layout.size

@Composable
fun SearchScreen(navController : NavHostController, viewModel: SearchViewModel){
    val state by viewModel.state.collectAsState()
    Scaffold(
        topBar = { TopAppBar("Cerca", navController) },
        bottomBar = { BottomAppBar(navController) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = state.searchText,
                onValueChange = { viewModel.onSearchTextChange(it)},
                label = { Text("Cerca eventi") },
                modifier = Modifier.fillMaxWidth()
            )
            viewModel.update(state.searchText)
            if(state.searchText.isNotBlank() && state.users.isNotEmpty()){

                LazyColumn(
                ) {
                    items(state.users) {user ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            AsyncImage(
                                model = user.avatarUrl,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = user.username,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                    }
                }

            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f)
            ) {
                items(state.posts) {post ->
                    AsyncImage(
                        model = post.postImage,
                        contentDescription = "Foto evento",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(1.dp)
                            .clickable{
                                navController.navigate(
                                    NavigationRoute.EventPost(post.idEvent)
                                )
                            }
                    )
                }
            }
        }
    }
}