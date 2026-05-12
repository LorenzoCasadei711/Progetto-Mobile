package com.example.progettomobile.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.progettomobile.data.supabase.Events
import com.example.progettomobile.data.supabase.Profiles
import com.example.progettomobile.data.supabase.SupabaseData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class ProfileState(
    val info : Profiles?,
    val events : List<Events?>
)

data class ProfileActions(
    val update : ()->(Unit),
    val logout : ()->(Unit)
)

class ProfileViewModel(private val data : SupabaseData) : ViewModel(){
    private val _info = MutableStateFlow<Profiles?>(null)
    private val _events = MutableStateFlow<List<Events?>>(emptyList())

    val state = combine(
        _info, _events
    ){info,events ->
        ProfileState(info, events)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        ProfileState(null, emptyList())
    )

    fun fetchInitialData() {
        viewModelScope.launch {
            try {
                _info.value = data.getUser()
                _events.value = data.getListEvents()
                println("Le informazioni fetchate sono: "+_info.value?.id_user)
            } catch (e: Exception) {
                e.printStackTrace()
                println("Informazioni non fetchate risultate in errore")
            }
        }
    }

    init {
        fetchInitialData()

    }

    val actions = ProfileActions(
        update = { fetchInitialData() },
        logout = {
            println("Logout action triggered. Scope active: ${viewModelScope.isActive}")

            // If the scope is inactive, we need to know why
            if (!viewModelScope.isActive) {
                println("WARNING: viewModelScope is already cancelled!")
            }

            viewModelScope.launch {
                try {
                    println("Coroutine started")
                    data.logout()
                    println("Data logout finished")
                } catch (e: Exception) {
                    println("Logout failed: ${e.message}")
                }
            }
        }
    )


}