package it.supabase.remembermy.ui.screens.auth

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.auth.exception.AuthRestException
import it.supabase.remembermy.data.supabase.SupabaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AccessState(
    val email : String,
    val password: String,
    var error : String,
    val isLoading : Boolean
)

data class AccessActions(
    val signUp : (email:String, password:String)-> Unit,
    val signIn : (email:String, password:String) -> Unit,
    val signInGit : ()-> Unit,
    val recoveryPassword : (email : String)->Boolean,
    val changePassword : (password : String)->Unit,
    val sendMagicLink : (email : String) -> Unit
)

class AccessViewModel (auth : SupabaseAuth) : ViewModel() {

    private val email = MutableStateFlow("")
    private val password = MutableStateFlow("")

    private val error = MutableStateFlow("")

    private val isLoading = MutableStateFlow(false)


    val state = combine(
        email, password, error, isLoading
    ) { email, password, error, isLoading ->
        AccessState(email, password, error, isLoading)
    }.stateIn(
        viewModelScope,
        SharingStarted.Companion.WhileSubscribed(),
        AccessState("","", "", false)
    )

    @OptIn(SupabaseInternal::class)
    val actions = AccessActions(
        signUp = {email, password->
            isLoading.value=true
            viewModelScope.launch {
                try {
                    auth.signUp(email, password)
                    error.value = ""
                } catch (e : AuthRestException){
                    println(e.description)
                    error.value = e.description.orEmpty().substringBefore(":")
                }
            }
            isLoading.value = false
        },
        signIn = { email, password ->
            isLoading.value = true
            viewModelScope.launch {
                try {
                    auth.signInEmail(email, password)
                    error.value = ""
                } catch(e : AuthRestException){
                    println(e.description)
                    error.value = e.description.orEmpty().substringBefore(":")
                }
            }
            isLoading.value = false
        },
        signInGit = {
            isLoading.value = true
            viewModelScope.launch {
                try {
                    auth.signInGit()
                    error.value = ""
                } catch (e: AuthRestException){
                    println(e.description)
                    error.value = e.description.orEmpty().substringBefore(":")
                }
            }
            isLoading.value = false
        },
        recoveryPassword = { email ->
            println("Started recovery form")
            var sent = MutableStateFlow(false)
            if(email.isEmpty()){
                println("Email value was found as empty: $email")
                return@AccessActions sent.value;
            }
            println("Email value wasn't empty $email")
            isLoading.value = true
            viewModelScope.launch {
                try {
                    auth.recoveryEmail(email)
                    sent.value = true
                    println("Email sent")
                } catch (e : Exception){
                    e.printStackTrace()
                    println("Job resulted in exception ${e.cause}")
                }
            }
            isLoading.value = false
            println("isLoading set to false")
            return@AccessActions sent.value;
        },
        changePassword = { password ->
            isLoading.value = true
            viewModelScope.launch {
                try {
                    auth.changePassword(password)
                } catch (e : AuthRestException){
                    e.printStackTrace()
                }
            }
            isLoading.value = false
        },
        sendMagicLink = {email ->
            isLoading.value = true
            viewModelScope.launch {
                try {
                    auth.signInOTP(email)
                } catch (e : Exception){
                    e.printStackTrace()
                }
            }
            isLoading.value = false
        }
    )
}