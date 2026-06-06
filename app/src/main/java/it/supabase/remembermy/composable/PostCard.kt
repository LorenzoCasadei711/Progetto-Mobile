package it.supabase.remembermy.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.progettomobile.composable.NavigationRoute

data class Post(
    val idEvent : String,
    val username: String,
    val userImage : String,
    val postImage : String,
    val likes: Int,
    val description : String,
    val position : String,
    val latitude : Double,
    val longitude : Double
)
@Composable
fun PostCard(
    navController : NavHostController,
    post : Post,
    isFollowed: Boolean,
    onFollowClick:()->Unit
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            AsyncImage(
                model = post.userImage,
                contentDescription = "Foto profilo",
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = post.username,
                fontWeight = FontWeight.Bold
            )
        }
        AsyncImage(
            model = post.postImage,
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
        ){

            IconButton(onClick = onFollowClick){
                Icon(
                    imageVector = if(isFollowed)
                        Icons.Default.Favorite
                    else
                        Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like icon"
                )
            }
            IconButton(onClick = {}){
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = "Comment icon"
                )
            }
            IconButton(onClick = {}){
                Icon(
                    imageVector = Icons.Default.CreditCard,
                    contentDescription = "Comment icon"
                )
            }

        }

        Text(
            text = "${post.likes} likes",
            modifier = Modifier.padding(12.dp),
            fontWeight = FontWeight.Bold
        )
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = post.username,
                modifier = Modifier.padding(horizontal = 6.dp),
                fontWeight = FontWeight.Bold
            )
            TextButton(
                onClick = {
                    navController.navigate(
                        NavigationRoute.Map(post.latitude, post.longitude, post.postImage))
                }
            ) {
                Text(post.position,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Thin,
                    fontStyle = FontStyle.Italic)
            }
            Text(
                text = post.description,
                modifier = Modifier.padding(horizontal = 6.dp)
            )
        }
    }
}