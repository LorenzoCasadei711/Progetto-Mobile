package it.supabase.remembermy.ui.screens.auth.magiclogin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.progettomobile.composable.NavigationRoute
import io.ktor.utils.io.InternalAPI
import it.supabase.remembermy.ui.screens.auth.AccessViewModel

@OptIn(InternalAPI::class)
@Composable
fun MagicLinkScreen(accessViewModel: AccessViewModel, navController: NavHostController) {

    var emailValue by remember {
        mutableStateOf("")
    }

    val errorText = accessViewModel.state.collectAsState().value.error

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.tertiary,
                            Color.Black
                        )

                    )
                )
        ) {}

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(vertical = 110.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Send a Link to your Email",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 30.dp)
            ){
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.2f))
                )
            }

            Spacer(Modifier.height(60.dp))

            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Email",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                TextField(
                    value = emailValue,
                    onValueChange = { it ->
                        emailValue = it
                    },
                    placeholder = {
                        Text(
                            text = "john.doe@example.com",
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.DarkGray,
                        focusedContainerColor = Color.DarkGray,
                        focusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        errorTextColor = Color.White,
                        errorContainerColor = Color.Red.copy(0.1f)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorText.isNotEmpty(),
                    supportingText = { Text(errorText, color = Color.White) }
                )
            }
            Spacer(Modifier.height(25.dp))

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = {
                    accessViewModel.actions.sendMagicLink(emailValue)
                    if(errorText.isEmpty() && emailValue.isNotEmpty()){
                        navController.navigate(NavigationRoute.Login){
                            popUpTo(0)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Send Email",
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(25.dp))

            TextButton(
                onClick = {
                    navController.navigate(NavigationRoute.Register) {
                        popUpTo(0)
                    }
                }
            ) {
                Text(text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Light,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    ) {
                        append("You are not registered? ")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    ) {
                        append("Register")
                    }
                })
            }
        }
    }
}