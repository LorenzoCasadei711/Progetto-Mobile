package it.supabase.remembermy.ui.screens.camera

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.progettomobile.data.Coordinates
import io.github.jan.supabase.postgrest.from
import it.supabase.remembermy.data.supabase.Events
import it.supabase.remembermy.data.supabase.supabase
import kotlinx.coroutines.launch
import java.util.UUID
import io.github.jan.supabase.auth.auth
import it.supabase.remembermy.data.supabase.Details
import it.supabase.remembermy.data.supabase.SupabaseData

class CameraViewModel (
    private val data: SupabaseData
) : ViewModel(){
    var pictureUri by mutableStateOf<Uri?>(null)
        private set
    var pictureCoordinates by mutableStateOf<Coordinates?>(null)
        private set
    fun setPictureData(uri: Uri,coordinates: Coordinates?){
        pictureUri = uri
        pictureCoordinates = coordinates
    }
    suspend fun createEvent(
        name: String,
        isPrivate: Boolean,
        date: String,
        details : String
    ){
        println("NOME EVENTO -> $name")
        println("URI -> $pictureUri")
        println("COORDINATE -> $pictureCoordinates")
        val uri = pictureUri ?: return
        val coordinates = pictureCoordinates ?: return
        val idUser = data.getCurrentUserId()

        val finalUri = data.fileToBucket(idUser, "events",null, uri)?: uri

        val event = Events(
            status_event = null,
            name_event = name,
            is_private = isPrivate,
            date_event = date,
            id_user = idUser,
            event_photo = finalUri.toString(),
            latitude = coordinates.latitude,
            longitude = coordinates.longitude,
            event_details = details,
        )

        data.saveEvent(event)


    }
}