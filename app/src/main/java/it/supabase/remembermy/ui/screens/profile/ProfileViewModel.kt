package it.supabase.remembermy.ui.screens.profile

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.storage.storage
import it.supabase.remembermy.data.database.Badges
import it.supabase.remembermy.data.supabase.Events
import it.supabase.remembermy.data.supabase.Profiles
import it.supabase.remembermy.data.supabase.SupabaseData
import it.supabase.remembermy.data.supabase.supabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

data class ProfileState(
    val info : Profiles?,
    val badges : List<Badges?>,
    val events : List<Events?>
)

data class ProfileActions(
    val update : ()->Unit,
    val editProfile: (profile: Profiles, localImageUri : Uri?)->Unit,
    val logout : ()->Unit
)

class ProfileViewModel(
    private val data : SupabaseData,
    private val contentResolver: ContentResolver
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
                var finalAvatarUrl = profile.avatar_url

                if (localImageUri != null && localImageUri.toString().startsWith("content://")) {
                    try {
                        val imageBytes = withContext(Dispatchers.IO) {
                            contentResolver.openInputStream(localImageUri)?.use { inputStream ->
                                inputStream.readBytes()
                            }
                        }

                        if (imageBytes != null) {
                            val fileName = "${UUID.randomUUID()}.jpg"

                            val storagePath = "avatars/${profile.id_user}/$fileName"

                            val bucket = supabase.storage.from("user-photos")

                            bucket.upload(storagePath, imageBytes) {
                                upsert = false
                            }

                            finalAvatarUrl = bucket.publicUrl(storagePath)
                        }
                    } catch (e: Exception) {
                        Log.e("SupabaseStorage", "Error uploading image", e)
                    }
                }

                val updatedProfile = profile.copy(avatar_url = finalAvatarUrl)
                try {
                    data.editProfile(updatedProfile)
                } catch (e: Exception) {
                    Log.e("Error During Edit Profile", e.message.toString())
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