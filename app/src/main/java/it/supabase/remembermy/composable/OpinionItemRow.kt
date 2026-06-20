package it.supabase.remembermy.composable

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import it.supabase.remembermy.R
import it.supabase.remembermy.data.supabase.Opinions

@Composable
fun OpinionRow(opinion : Opinions){
    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Row {
            Spacer(Modifier.width(4.dp))
            Image(
                painter = rememberAsyncImagePainter(opinion.profiles?.avatar_url),
                contentDescription = "Avatar URL",
                modifier = Modifier
                    .size(32.dp)
                    .padding(4.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.width(8.dp))
            (opinion.profiles?.nickname ?: opinion.profiles?.email)?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(opinion.review_opinion ?: "Errore Commento")
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
        )
    }
}