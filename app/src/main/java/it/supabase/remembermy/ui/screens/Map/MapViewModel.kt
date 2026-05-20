package it.supabase.remembermy.ui.screens.Map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.supabase.remembermy.data.supabase.Events
import it.supabase.remembermy.data.supabase.SupabaseData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MapState(
    val events: List<Events> = emptyList()
)
class MapViewModel (
    private val data: SupabaseData
): ViewModel(){
    private val _state = MutableStateFlow(MapState())
    val state: StateFlow<MapState> = _state
    init {
        fetchEvents()
    }
    fun fetchEvents(){
        viewModelScope.launch {
            try {
                val events = data.getListEvents()
                _state.value = MapState(
                    events.filterNotNull()
                )
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}