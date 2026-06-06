package it.supabase.remembermy.data.supabase

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.jwt.SharedJwkCache.set
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class SupabaseData(val supabase: SupabaseClient,
                   private val contentResolver: ContentResolver) {

    suspend fun getUser() = supabase.from("profiles").select{
        single()
           filter {
               eq("id_user", supabase.auth.retrieveUserForCurrentSession().id)
           }
    }.decodeAs<Profiles>()

    suspend fun getListEvents(): List<Events> {
        val currentUserId = getCurrentUserId()

        return supabase.from("events")
            .select(Columns.raw("*, followed_events(*), opinions(*, profiles(*))")) {
                filter {
                    eq("id_user", currentUserId)
                }
            }
            .decodeList<Events>()
    }

    suspend fun getListBadges(): List<UserBadge> {
        // 1. Grab the current user ID safely
        val userId = supabase.auth.retrieveUserForCurrentSession().id

        // 2. Query the junction table, embedding the related badge details
        return supabase.from("user_badges")
            .select(columns = Columns.raw("*, badges(*)")) {
                filter {
                    eq("id_user", userId)
                }
            }.decodeList<UserBadge>()
    }

    suspend fun getAllEvents() = supabase.from("events").select()
        .decodeList<Events>()

    suspend fun getProfileById(idUser: String) = supabase.from("profiles").select {
        single()
        filter {
            eq("id_user", idUser)
        }
    }.decodeAs<Profiles>()

    suspend fun editProfile(profile : Profiles) = supabase.from("profiles").update (profile
    ){
        filter {
            Profiles::id_user eq supabase.auth.retrieveUserForCurrentSession().id
        }
    }

    suspend fun postOpinion(opinion : Opinions) = supabase
        .from("opinions")
        .insert(opinion)

    suspend fun logout() = supabase.auth.signOut()

    suspend fun saveEvent(event: Events){
        try {
            println("PROVO A SALVARE EVENTO -> $event")

            supabase
                .from("events")
                .insert(event)

            println("EVENTO INSERITO OK")

        } catch (e: Exception) {
            println("ERRORE INSERIMENTO EVENTO -> ${e.message}")
            e.printStackTrace()
        }
    }

    suspend fun deleteEvent(event : Events){
        try {
            supabase.from("events").delete{
                filter {
                    Events::id_event eq event.id_event
                }
            }
        }catch (e : Exception){
            Log.e("ERROR", e.message?:"No Error Message Found")
        }
    }

    suspend fun fileToBucket(idUser : String, folder : String,oldImage : String?, localImageUri : Uri?) : String? {
        if (idUser.isNotEmpty() && localImageUri != null && localImageUri.toString().startsWith("content://")) {
            try {
                val imageBytes = withContext(Dispatchers.IO) {
                    contentResolver.openInputStream(localImageUri)?.use { inputStream ->
                        inputStream.readBytes()
                    }
                }

                if (imageBytes != null) {
                    val fileName = "${UUID.randomUUID()}.jpg"

                    val storagePath = "$folder/$idUser/$fileName"

                    val bucket = supabase.storage.from("user-photos")

                    bucket.upload(storagePath, imageBytes) {
                        upsert = false
                    }
                    if (!oldImage.isNullOrBlank() && oldImage.contains("user-photos/")) {
                        val oldStoragePath = oldImage.substringAfter("user-photos/")

                        try {
                            bucket.delete(oldStoragePath)
                            Log.d("StorageClean", "Successfully deleted old avatar: $oldStoragePath")
                        } catch (e: Exception) {
                            Log.e("StorageClean", "Failed to delete old avatar", e)
                        }
                    }
                    return bucket.publicUrl(storagePath)
                }
            } catch (e: Exception) {
                Log.e("SupabaseStorage", "Error uploading image", e)
            }
        }
        return null
    }

    suspend fun deleteBucketFile(path : String){
        if(path.isNotEmpty()){
            try {
                val bucket = supabase.storage.from("user-photos")
                val actualPath = path.substringAfter("user-photos")
                bucket.delete(actualPath)
            }catch (e : Exception){
                Log.d("Error", e.message?:"No Error Message Present")
            }
        }
    }

    suspend fun getCurrentUserId(): String{
        return supabase.auth
            .retrieveUserForCurrentSession()
            .id
    }
    suspend fun followEvent(idEvent: String){
        val idUser = getCurrentUserId()
        supabase.from("followed_events").insert(
            FollowedEvents(
                idUser,
                idEvent
            )
        )
    }
    suspend fun unfollowEvent(idEvent:String){
        val idUser = getCurrentUserId()
        supabase.from("followed_events").delete {
            filter {
                eq("id_user",idUser)
                eq("id_event",idEvent)
            }
        }
    }
    suspend fun isFollowingEvent(idEvent : String): Boolean{
        val idUser = getCurrentUserId()
        val result = supabase.from("followed_events").select {
            filter {
                eq("id_user",idUser)
                eq("id_event",idEvent)
            }
        }.decodeList<FollowedEvents>()
        return result.isNotEmpty()
    }
    suspend fun getMyFollowedEvents():List<Events>{
        val idUser = getCurrentUserId()

        val followed = supabase.from("followed_events").select {
            filter {
                eq("id_user",idUser)
            }
        }.decodeList<FollowedEvents>()

        return followed.mapNotNull { follow ->
            supabase.from("events").select {
                single()
                filter {
                    eq("id_event",follow.id_event)
                }
            }.decodeAs<Events>()
        }
    }
    suspend fun getMyCreatedAndFollowedEvents(): List<Events>{
        val created = getListEvents()
        val followed = getMyFollowedEvents()

        return (created + followed).distinctBy { it.id_event }
    }

    suspend fun getFollowedEventIds(): Set<String> {
        val idUser = getCurrentUserId()

        return supabase.from("followed_events").select {
            filter {
                eq("id_user", idUser)
            }
        }.decodeList<FollowedEvents>()
            .map { it.id_event }
            .toSet()
    }
}