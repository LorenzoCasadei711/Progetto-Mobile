package it.supabase.remembermy.ui.screens.Event

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.supabase.remembermy.composable.OpinionRow
import it.supabase.remembermy.data.supabase.Opinions
import it.supabase.remembermy.ui.screens.profile.ProfileViewModel

@Composable
fun OpinionSection(eventId : String, opinions : List<Opinions>, profileViewModel: ProfileViewModel){
    var myOpinion by remember {mutableStateOf("")}

    Column() {
        OutlinedTextField(
            value = myOpinion,
            onValueChange = {myOpinion = it},
            label = {Text("My Opinion")},
            trailingIcon = {
                IconButton(
                    onClick = {if(myOpinion.isNotEmpty()){
                        profileViewModel.actions.postOpinion(eventId, myOpinion)
                    } }
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, "Send Icon")
                }
            }

        )
        if (opinions.isEmpty()) {
            Text(
                text = "Non ci sono ancora recensioni per questo evento.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            opinions.forEach { opinion ->
                OpinionRow(opinion)
            }
        }
    }
}