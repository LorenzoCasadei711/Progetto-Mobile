package it.supabase.remembermy.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val error : String
)

data class AccessActions(
    val signUp : (email:String, password:String)-> Unit,
    val signIn : (email:String, password:String) -> Unit,
    val signInGit : ()-> Unit,
    val signIngGoogle : ()-> Unit,
    val recoveryPassword : ()->Boolean
)

class AccessViewModel (auth : SupabaseAuth) : ViewModel() {

    private val email = MutableStateFlow("")
    private val password = MutableStateFlow("")

    private var error = MutableStateFlow("")


    val state = combine(
        email, password, error
    ) { email, password, error ->
        AccessState(email, password, error)
    }.stateIn(
        viewModelScope,
        SharingStarted.Companion.WhileSubscribed(),
        AccessState("","", "")
    )

    val actions = AccessActions(
        signUp = {email, password->
            viewModelScope.launch {
                try {
                    auth.signUp(email, password)
                    error.value = ""
                } catch (e : AuthRestException){
                    println(e.description)
                    error.value = e.description.orEmpty().substringBefore(":")
                }
            }

        },
        signIn = { email, password ->
            viewModelScope.launch {
                try {
                    auth.signInEmail(email, password)
                    error.value = ""
                } catch(e : AuthRestException){
                    println(e.description)
                    error.value = e.description.orEmpty().substringBefore(":")
                }
            }
        },
        signInGit = {
            viewModelScope.launch {
                try {
                    auth.signInGit()
                    error.value = ""
                } catch (e: AuthRestException){
                    println(e.description)
                    error.value = e.description.orEmpty().substringBefore(":")
                }
            }
        },
        signIngGoogle = {
            viewModelScope.launch {
                try {
                    auth.signInGoogle()
                    error.value = ""
                } catch (e: AuthRestException){
                    println(e.description)
                    error.value = e.description.orEmpty().substringBefore(":")
                }
            }
        },
        recoveryPassword = {
            var sent = MutableStateFlow(false)
            if(email.value.isEmpty()){
                return@AccessActions sent.value;
            }
            viewModelScope.launch {
                try {
                    auth.recoveryEmail(email.value)
                    sent.value = true
                } catch (e : Exception){
                    e.printStackTrace()
                }
            }
            return@AccessActions sent.value;
        }
    )
}