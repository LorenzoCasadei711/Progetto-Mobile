package com.example.progettomobile.ui.screens.access

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progettomobile.data.supabase.SupabaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AccessState(
    val email : String,
    val password: String
)

data class AccessActions(
    val signIn : (email:String, password:String) -> Unit
)
class AccessViewModel (auth : SupabaseAuth ) : ViewModel() {

    private val email = MutableStateFlow("")
    private val password = MutableStateFlow("")

    val state = combine(
        email, password
    ){email, password ->
        AccessState(email, password)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        AccessState("","")
    )

    val actions = AccessActions(
        signIn = { email, password ->
            viewModelScope.launch { auth.signInEmail(email, password) }
        }
    )
}