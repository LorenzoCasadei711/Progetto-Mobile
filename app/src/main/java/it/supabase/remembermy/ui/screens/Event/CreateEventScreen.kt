package it.supabase.remembermy.ui.screens.Event

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import it.supabase.remembermy.ui.screens.camera.CameraViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Scaffold
import it.supabase.remembermy.composable.TopAppBar
import com.example.progettomobile.composable.BottomAppBar
import com.example.progettomobile.composable.NavigationRoute
import kotlinx.coroutines.launch

@Composable
fun CreateEventScreen(
    navController: NavHostController,
    vm: CameraViewModel
){
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }
    var details by remember {mutableStateOf("")}

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = { TopAppBar("Creazione Evento", navController) },
        bottomBar = {BottomAppBar(navController)}

    ) {paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Crea evento")

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nome evento") }
            )

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Data evento") }
            )

            OutlinedTextField(
                value = details,
                onValueChange = { details = it },
                label = { Text("Dettagli evento") }
            )

            Row {
                Checkbox(
                    checked = isPrivate,
                    onCheckedChange = { isPrivate = it }
                )
                Text("Evento privato")
            }

            Button(
                onClick = {
                    scope.launch {
                        vm.createEvent(
                            name = name,
                            isPrivate = isPrivate,
                            date = date,
                            details = details
                        )

                        navController.navigate(NavigationRoute.Map)
                    }
                }
            ) {
                Text("Crea il tuo evento")
            }
        }
    }
}