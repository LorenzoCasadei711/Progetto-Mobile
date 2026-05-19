package it.supabase.remembermy.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.supabase.remembermy.data.supabase.Events
import it.supabase.remembermy.data.supabase.Profiles
import it.supabase.remembermy.data.supabase.SupabaseData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileState(
    val info : Profiles?,
    val events : List<Events?>
)

data class ProfileActions(
    val update : ()->Unit,
    val editProfile: (Profiles)->Unit,
    val logout : ()->Unit
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
                println(data.getUser())
                _info.value = data.getUser()
                _events.value = data.getListEvents()
                println("Le informazioni fetchate sono: "+_events.value)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    init {
        fetchInitialData()

    }

    val actions = ProfileActions(
        update = { fetchInitialData() },
        editProfile = {profile->
            viewModelScope.launch {
                try {
                    data.editProfile(profile)
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }

        },
        logout = {
            viewModelScope.launch {
                try {
                    data.logout()
                } catch (e: Exception) {
                    println("Logout failed: ${e.message}")
                }
            }
        }
    )


}