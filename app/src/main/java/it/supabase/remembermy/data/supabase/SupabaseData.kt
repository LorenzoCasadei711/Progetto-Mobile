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


    suspend fun getUser(): Profiles? {
        val userId = supabase.auth.currentSessionOrNull()?.user?.id ?: return null

        return supabase.from("profiles").select {
            single()
            filter {
                eq("id_user", userId)
            }
        }.decodeAs<Profiles>()
    }

    suspend fun getListEvents(): List<Events> {
        val userId = this.getCurrentUserId()
        if(userId.isEmpty()) throw IllegalStateException("No User logged out")
        return supabase.from("events")
            .select(Columns.raw("*, followed_events(*), opinions(*, profiles(*))")) {
                filter {
                    eq("id_user", userId)
                }
            }
            .decodeList<Events>()
    }

    suspend fun getListBadges(): List<UserBadge> {
        val userId = this.getCurrentUserId()
        if(userId.isEmpty()) throw IllegalStateException("No User logged out")

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

    suspend fun editProfile(profile : Profiles) {
        val userId = getCurrentUserId()
        if (userId.isNotEmpty()) {
            supabase.from("profiles").update(profile) {
                filter {
                    Profiles::id_user eq userId
                }
            }
        } else{
            Log.e("EDIT_PROFILE", "this is the userId $userId")
        }
    }

    suspend fun editEvent(event : Events) = supabase.from("events").update(event){
        filter {
            Events::id_user eq supabase.auth.retrieveUserForCurrentSession().id
            Events::id_event eq event.id_event
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

    fun getCurrentUserId(): String{
        return supabase.auth.currentUserOrNull()?.id?:""
    }
    suspend fun followEvent(idEvent: String){
        val userId = this.getCurrentUserId()
        if(userId.isEmpty()) throw IllegalStateException("No User logged out")
        supabase.from("followed_events").insert(
            FollowedEvents(
                userId,
                idEvent
            )
        )
    }
    suspend fun unfollowEvent(idEvent:String){
        val userId = this.getCurrentUserId()
        if(userId.isEmpty()) throw IllegalStateException("No User logged out")
        supabase.from("followed_events").delete {
            filter {
                eq("id_user",userId)
                eq("id_event",idEvent)
            }
        }
    }
    suspend fun isFollowingEvent(idEvent : String): Boolean{
        val userId = this.getCurrentUserId()
        if(userId.isEmpty()) throw IllegalStateException("No User logged out")
        val result = supabase.from("followed_events").select {
            filter {
                eq("id_user",userId)
                eq("id_event",idEvent)
            }
        }.decodeList<FollowedEvents>()
        return result.isNotEmpty()
    }
    suspend fun getMyFollowedEvents():List<Events>{
        val userId = this.getCurrentUserId()
        if(userId.isEmpty()) throw IllegalStateException("No User logged out")

        val followed = supabase.from("followed_events").select {
            filter {
                eq("id_user",userId)
            }
        }.decodeList<FollowedEvents>()

        return followed.mapNotNull { follow ->
            supabase.from("events").select(Columns.raw("*, followed_events(*), opinions(*, profiles(*))")) {
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
        val userId = this.getCurrentUserId()
        if(userId.isEmpty()) throw IllegalStateException("No User logged out")
        return supabase.from("followed_events").select {
            filter {
                eq("id_user", userId)
            }
        }.decodeList<FollowedEvents>()
            .map { it.id_event }
            .toSet()
    }
    suspend fun getOpinionsByEvent(idEvent: String): List<Opinions>{
        return supabase.from("opinions").select {
            filter {
                eq("event_id",idEvent)
            }
        }.decodeList<Opinions>()
    }

    suspend fun addOpinion(idEvent: String, review: String){
        val idUser = getCurrentUserId()

        val opinion = Opinions(
            idUser,
            idEvent,
            null,
            review,
            null
        )

        supabase.from("opinions").insert(opinion)
    }
    suspend fun searchUsers(query: String): List<Profiles> {
        if(query.isBlank()) return emptyList()

        val byNickname = supabase.from("profiles").select {
            filter {
                ilike("nickname","%$query")
            }
        }.decodeList<Profiles>()

        val byEmail = supabase.from("profiles").select {
            filter {
                ilike("email","%$query%")
            }
        }.decodeList<Profiles>()

        return (byNickname + byEmail).distinctBy { it.id_user }
    }

    suspend fun searchEventsByTag(query: String): List<Events> {
        if(query.isBlank()) return emptyList()

        val tags = supabase.from("tags").select {
            filter {
                ilike("name_tag","%$query%")
            }
        }.decodeList<Tags>()

        val eventIds = tags.flatMap { tag ->
            supabase.from("event_tags").select {
                filter{
                    eq("id_tag",tag.id_tag!!)
                }
            }.decodeList<EventTag>()
        }.map { it.id_event }

        return eventIds.mapNotNull { idEvent ->
            supabase.from("events").select {
                single()
                filter {
                    eq("id_event",idEvent)
                }
            }.decodeAs<Events>()
        }
    }
    suspend fun searchEventsByName(query: String): List<Events>{
        return supabase.from("events").select {
            filter {
                ilike("name_event","%$query%")
            }
        }.decodeList<Events>()
    }
    suspend fun searchEvents(query: String):List<Events>{
        if(query.isBlank()) return getAllEvents()

        val eventsByName = searchEventsByName(query)

        val usersFound = searchUsers(query)

        val eventsByTag = searchEventsByTag(query)

        val eventsByUser =usersFound.flatMap { profile ->
            supabase.from("events").select {
                filter {
                    eq("id_user",profile.id_user)
                }
            }.decodeList<Events>()
        }
        return (eventsByName + eventsByUser + eventsByTag).distinctBy { it.id_event }
    }

    private suspend fun createTag(name:String): Tags{
        val tag = Tags(
            name_tag = name,
            id_user = getCurrentUserId()
        )

        return supabase.from("tags").insert(tag){
            select()
        }.decodeSingle<Tags>()

    }

    suspend fun addTagToEvent(idEvent: String, idTag : String) {
        supabase.from("event_tags").insert(
            EventTag(
                id_event = idEvent,
                id_tag = idTag
            )
        )
    }

    suspend fun saveEventWithTag(event: Events,tagName: String){
        val savedEvent = supabase.from("events")
            .insert(event) {
                select()
            }.decodeSingle<Events>()

        if (tagName.isNotBlank()){
            val tag = createTag(tagName)
            addTagToEvent(savedEvent.id_event!!,tag.id_tag!!)
        }
    }
}