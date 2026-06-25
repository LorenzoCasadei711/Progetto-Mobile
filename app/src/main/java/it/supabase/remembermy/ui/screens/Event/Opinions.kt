package it.supabase.remembermy.ui.screens.Event

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import it.supabase.remembermy.composable.OpinionRow
import it.supabase.remembermy.data.supabase.Opinions
import it.supabase.remembermy.ui.screens.profile.ProfileViewModel
import org.koin.compose.koinInject

@Composable
fun OpinionSection(eventId : String, opinions : List<Opinions>){
    var myOpinion by remember {mutableStateOf("")}
    var allOpinions by remember { mutableStateOf(opinions) }
    val profileViewModel = koinInject<ProfileViewModel>()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(100.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            value = myOpinion,
            onValueChange = {myOpinion = it},
            label = {Text("My Opinion")},
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (myOpinion.isNotEmpty()) {
                        val newOpinion = profileViewModel.actions.postOpinion(eventId, myOpinion)
                        if(newOpinion != null){
                            allOpinions += newOpinion
                        }
                    }
                    myOpinion = ""
                }
            ),
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (myOpinion.isNotEmpty()) {
                            val newOpinion = profileViewModel.actions.postOpinion(eventId, myOpinion)
                            if(newOpinion != null){
                                allOpinions += newOpinion
                            }
                        }
                        myOpinion = ""
                    }
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, "Send Icon")
                }
            }

        )
        if (allOpinions.isEmpty()) {
            Text(
                text = "Non ci sono ancora recensioni per questo evento.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 5.dp)
            )
        } else {
            allOpinions.forEach { opinion ->
                OpinionRow(opinion)
            }
        }
    }
}