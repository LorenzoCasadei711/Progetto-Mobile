package it.supabase.remembermy.ui.screens.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.supabase.remembermy.data.database.Badges
import it.supabase.remembermy.data.supabase.Events
import it.supabase.remembermy.data.supabase.Opinions
import it.supabase.remembermy.data.supabase.Profiles
import it.supabase.remembermy.data.supabase.SupabaseData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileState(
    val info : Profiles?,
    val badges : List<Badges?>,
    val events : List<Events?>
)

data class ProfileActions(
    val update : ()->Unit,
    val editProfile: (profile: Profiles, localImageUri : Uri?)->Unit,
    val editEvent : (event : Events, localImageUri : Uri?) -> Unit,
    val deleteEvent : (event : Events) -> Unit,
    val postOpinion : (eventId : String, reviewOpinion : String) -> Unit,
    val logout : ()->Unit
)

class ProfileViewModel(
    private val data : SupabaseData
) : ViewModel(){
    private val _info = MutableStateFlow<Profiles?>(null)

    private val _badges = MutableStateFlow<List<Badges?>>(emptyList())
    private val _events = MutableStateFlow<List<Events?>>(emptyList())

    val state = combine(
        _info, _badges, _events
    ){info,badges,events ->
        ProfileState(info,badges, events)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        ProfileState(null, emptyList(),emptyList())
    )

    fun fetchInitialData() {
        viewModelScope.launch {
            try {
                _info.value = data.getUser()
                _events.value = data.getListEvents()
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
        }
    )


}
