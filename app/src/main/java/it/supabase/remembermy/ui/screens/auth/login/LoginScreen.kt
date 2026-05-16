package it.supabase.remembermy.ui.screens.auth.login

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import it.supabase.remembermy.R
import com.example.progettomobile.composable.NavigationRoute
import io.ktor.utils.io.InternalAPI
import it.supabase.remembermy.ui.screens.auth.AccessViewModel
import androidx.compose.runtime.collectAsState
import it.supabase.remembermy.composable.LoadingImage

@OptIn(InternalAPI::class)
@Composable
fun LoginScreen(accessViewModel: AccessViewModel, navController : NavHostController){

    var emailValue by remember {
        mutableStateOf("")
    }

    var passwordValue by remember {
        mutableStateOf("")
    }

    var isPasswordHidden by remember {
        mutableStateOf(true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.TopCenter
    ){
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
        ){}

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(vertical = 110.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Log Into Your Account",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Enter your personal data to access your account",
                style= MaterialTheme.typography.bodyMedium,
                color = Color.White
            )

            Spacer(Modifier.height(8.dp))
            //Github SignIn Button
            OutlinedButton(
                onClick = {accessViewModel.actions.signInGit()},
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.github_logo_icon_143772),
                    contentDescription = "Github Logo",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Login With Github",
                    color=Color.White)
            }
            //Magic Link Login
            OutlinedButton(
                onClick = {navController.navigate(NavigationRoute.MagicLink)},
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(R.drawable.gmail_new_logo_icon_159149),
                    contentDescription = "Github Logo",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Login With A One Time Link",
                    color=Color.White)
            }

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

                Text(
                    text = "Or",
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(Color.White.copy(alpha = 0.2f))
                )
            }
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = "Email",
                    color=Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                TextField(
                    value = emailValue,
                    onValueChange = { it->
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
                    isError = accessViewModel.state.collectAsState().value.error.contains("credentials") || accessViewModel.state.collectAsState().value.error.contains("email"),
                    supportingText = {Text(accessViewModel.state.collectAsState().value.error, color = Color.White)}
                )
            }
            Spacer(Modifier.height(25.dp))
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(text = "Password",
                    color=Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                TextField(
                    value = passwordValue,
                    onValueChange = { it->
                        passwordValue = it
                    },
                    placeholder = {
                        Text(
                            text = "Enter your password",
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
                    visualTransformation = if(isPasswordHidden) PasswordVisualTransformation()
                    else VisualTransformation.None,
                    modifier = Modifier.fillMaxWidth(),
                    isError = accessViewModel.state.collectAsState().value.error.isNotEmpty(),
                    trailingIcon = {
                        IconButton(
                            onClick = {isPasswordHidden=!isPasswordHidden},
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = Color.White
                            )
                        ) {
                            if(isPasswordHidden){
                                Icon(Icons.Default.RemoveRedEye, "Open Eye")
                            }else{
                                Icon(Icons.Default.HorizontalRule, "Open Eye")

                            }
                        }
                    }
                )
                TextButton(
                    onClick = {navController.navigate(NavigationRoute.Recovery){
                        popUpTo(0)
                    } }
                ) {
                    Text(text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Light,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        ){
                            append("Forgot Password? ")
                        }
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        ){
                            append("Send Recovery Email")
                        }
                    })
                }
            }
            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = {
                    accessViewModel.state.value.error = ""
                    accessViewModel.actions.signIn(emailValue, passwordValue)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if(!accessViewModel.state.collectAsState().value.isLoading){
                    Text(text = "Sign in",
                        modifier=Modifier.padding(vertical = 4.dp),
                        color = Color.Black)
                }else{
                    LoadingImage()
                }

            }

            Spacer(modifier = Modifier.height(25.dp))
            TextButton(
                onClick = {
                    accessViewModel.state.value.error = ""
                    navController.navigate(NavigationRoute.Register){
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
                    ){
                        append("Don't have an account? ")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    ){
                        append("Register")
                    }
                })
            }
        }
    }
}