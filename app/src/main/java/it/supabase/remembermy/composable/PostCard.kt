package it.supabase.remembermy.composable

import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

data class Post(
    val username: String,
    val userImage : String,
    val postImage : String,
    val likes: Int,
    val description : String
)
@Composable
fun PostCard(post : Post) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(bottom = 12.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
        ) {
            var isPressed by remember { mutableStateOf(false) }
            IconButton(onClick = { isPressed = !isPressed }) {
                Icon(
                    imageVector = if (isPressed)
                        Icons.Default.Favorite
                    else
                        Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like icon"
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = "Comment icon"
                )
            }
            IconButton(onClick = {}) {
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
        Text(
            text = "${post.username} ${post.description}",
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}