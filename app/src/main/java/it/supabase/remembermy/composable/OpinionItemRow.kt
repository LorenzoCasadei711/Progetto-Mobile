package it.supabase.remembermy.composable

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
            Image(
                painter = rememberAsyncImagePainter(
                    model = opinion.profile?.avatar_url,
                    error = painterResource(R.drawable.profile_simple_svgrepo_com),
                    placeholder = painterResource(R.drawable.profile_simple_svgrepo_com)
                ),
                contentDescription = "Avatar URL",
                modifier = Modifier
                    .size(32.dp)
                    .padding(4.dp)
                    .clip(CircleShape)
            )
            Spacer(Modifier.width(16.dp))
            (opinion.profile?.nickname ?: opinion.profile?.email)?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(opinion.review_opinion ?: "Errore Commento")
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
        )
    }
}