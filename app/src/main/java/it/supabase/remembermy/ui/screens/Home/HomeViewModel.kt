package it.supabase.remembermy.ui.screens.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.supabase.remembermy.composable.OSMState
import it.supabase.remembermy.composable.Post
import it.supabase.remembermy.composable.PostCard
import it.supabase.remembermy.composable.rememberOSM
import it.supabase.remembermy.data.repository.OSMDataSource
import it.supabase.remembermy.data.supabase.SupabaseData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeState(
    val posts: List<Post> = emptyList(),
    val followedEvents: Set<String> = emptySet()
)
class HomeViewModel (
    private val data: SupabaseData,
    private val osmDataSource: OSMDataSource
) : ViewModel(){
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    init {
        fetchPosts()
    }

    fun fetchPosts(){
        viewModelScope.launch {
            try {
                val events = data.getMyCreatedAndFollowedEvents()
                val followedIds = data.getFollowedEventIds()
                _state.value = HomeState(
                    followedEvents = followedIds,
                    posts = events.map { event ->
                        val profile = data.getProfileById(event.id_user)
                        Post(
                            idEvent = event.id_event!!,
                            username = profile.nickname ?: profile.email,
                            userImage = profile.avatar_url ?: "https://picsum.photos/100",
                            postImage = event.event_photo ?: "https://picsum.photos/100",
                            likes = 0,
                            description = event.name_event,
                            latitude = event.latitude,
                            longitude = event.longitude,
                            position = event.place_name?:""
                        )
                    }
                )
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
    fun togleFollow(idEvent: String){
        viewModelScope.launch {
            try {
                val followed = data.isFollowingEvent(idEvent)

                if(followed){
                    data.unfollowEvent(idEvent)
                }else{
                    data.followEvent(idEvent)
                }
                fetchPosts()
            }catch (e: Exception){
                e.printStackTrace()
            }

        }
    }
}