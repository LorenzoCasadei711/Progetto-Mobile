package com.example.progettomobile.ui.screens.access

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.progettomobile.composable.TopAppBar
import io.ktor.utils.io.InternalAPI

@OptIn(InternalAPI::class)
@Composable
fun ToAccessScreen(accessViewModel: AccessViewModel, navController : NavHostController){
    Scaffold(
        topBar = { TopAppBar("Login", navController) },
        modifier = Modifier
            .fillMaxSize()
    ) { innerPadding->
        Column(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            var email : String by rememberSaveable(){ mutableStateOf("") }
            OutlinedTextField(
                value = email,
                onValueChange = {email = it},
                label = { Text("Email") },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
            Spacer(Modifier.padding(16.dp))
            var password : String by rememberSaveable(){ mutableStateOf("") }
            var visualTransformation : VisualTransformation by remember() {mutableStateOf(
                PasswordVisualTransformation()) }
            val state by accessViewModel.state.collectAsState()
            OutlinedTextField(
                value = password,
                onValueChange = {password = it},
                label = { Text("Password") },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                visualTransformation = visualTransformation,
                trailingIcon = {
                    IconButton(
                        onClick = {
                            visualTransformation = if(visualTransformation == VisualTransformation.None)
                                PasswordVisualTransformation()
                            else
                                VisualTransformation.None
                        }
                    ) {
                        Icon(Icons.Default.RemoveRedEye, "Eye")
                    }
                },
                isError = !state.error.isEmpty(),
                supportingText = {
                    Text(state.error)
                }
            )
            Spacer(Modifier.padding(16.dp))
            Button(
                onClick = {
                    accessViewModel.actions.signIn(email, password)
                }
            ) {
                Text("Sign In")
            }
            Button(
                onClick = {
                    accessViewModel.actions.signUp(email, password)
                    println("Il support text: " + state)
                }
            ) {
                Text("Sign Up")
            }
            Button(
                onClick = {
                    accessViewModel.actions.signInGit()
                }
            ) {
                Text("Sign In With Github")
            }
        }
    }
}