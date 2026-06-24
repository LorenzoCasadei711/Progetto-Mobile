package it.supabase.remembermy.ui.screens.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.supabase.remembermy.data.database.Badges
import it.supabase.remembermy.data.supabase.Events
import it.supabase.remembermy.data.supabase.FollowedEvents
import it.supabase.remembermy.data.supabase.Opinions
import it.supabase.remembermy.data.supabase.Profiles
import it.supabase.remembermy.data.supabase.SupabaseData
import it.supabase.remembermy.data.supabase.UserBadge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileState(
    val info : Profiles?,
    val badges : List<UserBadge?>,
    val events : List<Events?>,
    val followedEvents: Set<String>,
    var idUser : String
)
data class ProfileActions(
    val update : (userId : String?)->Unit,
    val editProfile: (profile: Profiles, localImageUri : Uri?)->Unit,
    val editEvent : (event : Events, localImageUri : Uri?) -> Unit,
    val deleteEvent : (event : Events) -> Unit,
    val postOpinion : (eventId : String, reviewOpinion : String) -> Unit,
    val logout : ()->Unit,
    val toggleFollow : (idEvent : String)->Unit
)

class ProfileViewModel(
    private val data : SupabaseData
) : ViewModel(){
    private val _info = MutableStateFlow<Profiles?>(null)
    private val _badges = MutableStateFlow<List<UserBadge?>>(emptyList())
    private val _events = MutableStateFlow<List<Events?>>(emptyList())
    private val _followedEvents = MutableStateFlow<Set<String>>(emptySet())
    private val _idUser = MutableStateFlow<String>("")
    private var currentViewedUserId: String = ""

    val state = combine(
        _info, _badges, _events, _followedEvents, _idUser
    ){info,badges,events, followedEvents, idUser ->
        ProfileState(info,badges, events, followedEvents, idUser)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        ProfileState(null, emptyList(),emptyList(), emptySet(), "")
    )

    fun fetchInitialData(targetUserId : String?) {
        val resolvedId = if (targetUserId.isNullOrEmpty()) {
            data.getCurrentUserId()
        } else {
            targetUserId
        }
        if (resolvedId == currentViewedUserId && _info.value != null) {
            return
        }
        currentViewedUserId = resolvedId
        viewModelScope.launch {
            try {
                if (_idUser.value.isEmpty()) {
                    _idUser.value = data.getCurrentUserId()
                }
                _info.value = data.getUser(resolvedId)
                _events.value = data.getListEvents(resolvedId)
                _badges.value = data.getListBadges(resolvedId)
                _followedEvents.value = data.getFollowedEventIds(resolvedId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val actions = ProfileActions(
        update = { userId ->
            fetchInitialData(userId)
                 },
        editProfile = { profile, localImageUri ->
            viewModelScope.launch {
                var finalAvatarUrl = data.fileToBucket(profile.id_user, "avatars", state.value.info?.avatar_url?:"", localImageUri)?: profile.avatar_url

                val updatedProfile = profile.copy(avatar_url = finalAvatarUrl)
                try {
                    data.editProfile(updatedProfile)
                } catch (e: Exception) {
                    Log.e("Edit Profile", e.message.toString())
                }
            }
        },
        deleteEvent = {event ->
            viewModelScope.launch {
                data.deleteBucketFile(event.event_photo?:"")
                data.deleteEvent(event)
            }
        },
        postOpinion = {eventId, reviewOpinion->
            viewModelScope.launch {
                try {
                    val opinion = Opinions(
                        id_user = data.getCurrentUserId(),
                        id_event = eventId,
                        id_opinion = null,
                        review_opinion = reviewOpinion,
                        profiles = null
                    )
                    data.postOpinion(opinion)
                }catch (e : Exception){
                    Log.e("ERROR-PostOpinion", e.message.toString())
                }

            }
        } ,
        editEvent = {event, localImageUri ->
            viewModelScope.launch {
                try {
                    val idUser = data.getCurrentUserId()
                    var finalUri = localImageUri.toString()
                    if(!localImageUri.toString().contains("https")){
                        finalUri = data.fileToBucket(idUser, "events",null, localImageUri)?: localImageUri.toString()
                    }
                    data.editEvent(event.copy(event_photo = finalUri))
                } catch (e : Exception){
                    Log.e("ERROR-EditEvent", e.message.toString())
                }

            }
        },
        logout = {
            viewModelScope.launch {
                try {
                    data.logout()
                } catch (e: Exception) {
                    Log.e("Logout Failed", e.message.toString())
                }
            }
        },
        toggleFollow = { idEvent->
                viewModelScope.launch {
                    try {
                        val followed = data.isFollowingEvent(idEvent)

                        if(followed){
                            data.unfollowEvent(idEvent)
                        }else{
                            data.followEvent(idEvent)
                        }

                    }catch (e: Exception){
                        e.printStackTrace()
                    }

                }

        }
    )
}
