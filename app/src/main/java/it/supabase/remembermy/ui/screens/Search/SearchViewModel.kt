package it.supabase.remembermy.ui.screens.Search

import androidx.lifecycle.ViewModel
import it.supabase.remembermy.data.supabase.SupabaseData
import it.supabase.remembermy.composable.Post
import it.supabase.remembermy.composable.PostCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
data class SearchState(
    val posts: List<Post> = emptyList(),
    val followedEvents: Set<String> = emptySet()
)
class SearchViewModel (
    private val data: SupabaseData
): ViewModel(){
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state

    init {
        fetchAllPosts()
    }
    fun fetchAllPosts() {
        viewModelScope.launch {
            try {
                val events = data.getAllEvents()
                val followedIds = data.getFollowedEventIds()
                _state.value = SearchState(
                    followedEvents = followedIds,
                    posts = events.map { event ->
                        val profile = data.getProfileById(event.id_user)
                        Post(
                            idEvent = event.id_event ?: "",
                            username = profile.nickname ?: profile.email,
                            userImage = profile.avatar_url ?: "https://picsum.photos/100",
                            postImage = "https://picsum.photos/100",
                            likes = 0,
                            description = event.name_event
                        )
                    }
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun toggleFollow(idEvent: String) {
        viewModelScope.launch {
            try {
                val followed = idEvent in _state.value.followedEvents

                if (followed) {
                    data.unfollowEvent(idEvent)
                } else {
                    data.followEvent(idEvent)
                }

                fetchAllPosts()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}