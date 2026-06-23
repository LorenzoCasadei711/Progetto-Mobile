package it.supabase.remembermy.ui.screens.Search

import androidx.lifecycle.ViewModel
import it.supabase.remembermy.data.supabase.SupabaseData
import it.supabase.remembermy.composable.Post
import it.supabase.remembermy.composable.PostCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import androidx.room.util.query
import it.supabase.remembermy.data.repository.OSMDataSource
import kotlinx.coroutines.launch

data class UserResult(
    val idUser: String,
    val username: String,
    val avatarUrl : String?
)
data class SearchState(
    val posts: List<Post> = emptyList(),
    val followedEvents: Set<String> = emptySet(),
    val searchText: String = "",
    val users: List<UserResult> = emptyList()
)
class SearchViewModel (
    private val data: SupabaseData,
    private val osmDataSource: OSMDataSource
): ViewModel(){
    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state

    init {
        search("")
    }
    fun fetchAllPosts() {
        viewModelScope.launch {
            try {
                val events = data.getAllEvents()
                val followedIds = data.getFollowedEventIds()
                val profileByUserId = events
                    .map{it.id_user}
                    .distinct()
                    .associateWith{ idUser ->
                        data.getProfileById(idUser)
                    }

                _state.value = _state.value.copy(
                    followedEvents = followedIds,
                    posts = events.map { event ->
                        val position = osmDataSource.searchWithCoordinates(event.latitude, event.longitude).displayName

                        val profile = profileByUserId[event.id_user]
                        Post(
                            idEvent = event.id_event ?: "",
                            username = profile?.nickname ?: profile?.email ?: "Utente",
                            userImage = profile?.avatar_url ?: "https://picsum.photos/100",
                            postImage = event.event_photo ?:"https://picsum.photos/100",
                            likes = 0,
                            description = event.name_event,
                            latitude = event.latitude,
                            longitude = event.longitude,
                            position = position,
                            opinion = event.opinions
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
    fun update(){
        search("")
    }
    private fun search(query: String) {
        viewModelScope.launch {
            try {
                val events = data.searchEvents(query)
                val users = data.searchUsers(query)
                val followedIds = data.getFollowedEventIds()


                val profileByUserId = events
                    .map { it.id_user }
                    .distinct()
                    .associateWith { data.getProfileById(it) }

                _state.value = _state.value.copy(
                    users = users.map {
                        UserResult(
                            idUser = it.id_user,
                            username = it.nickname ?: it.email,
                            avatarUrl = it.avatar_url
                        )
                    },
                    followedEvents = followedIds,
                    posts = events.map{ event ->
                        val profile = profileByUserId[event.id_user]
                        Post(
                            idEvent = event.id_event ?: "",
                            username = profile?.nickname ?: profile?.email ?: "Utente",
                            userImage = profile?.avatar_url ?: "https://picsum.photos/100",
                            postImage = event.event_photo ?: "https://picsum.photos/100",
                            likes = 0,
                            description = event.name_event,
                            latitude = event.latitude,
                            longitude = event.longitude,
                            position = event.place_name?:"Place Name",
                            opinion = event.opinions
                        )
                    }
                )
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun onSearchTextChange(text: String) {
        _state.value = _state.value.copy(searchText = text)
        search(text)
    }
}