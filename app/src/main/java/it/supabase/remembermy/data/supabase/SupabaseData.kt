package it.supabase.remembermy.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.jwt.SharedJwkCache.set
import io.github.jan.supabase.postgrest.from

class SupabaseData(val supabase: SupabaseClient) {

    suspend fun getUser() = supabase.from("profiles").select{
        single()
           filter {
               eq("id_user", supabase.auth.retrieveUserForCurrentSession().id)
           }
    }.decodeAs<Profiles>()

    suspend fun getListEvents() = supabase.from("events").select {
        filter {
            eq("id_user", supabase.auth.retrieveUserForCurrentSession().id)
        }
    }.decodeList<Events>()

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

    suspend fun getCurrentUserId(): String{
        return supabase.auth
            .retrieveUserForCurrentSession()
            .id
    }
    suspend fun followEvent(idEvent: String){
        val idUser = getCurrentUserId()
        supabase.from("followed_events").insert(
            FollowedEvent(
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
        }.decodeList<FollowedEvent>()
        return result.isNotEmpty()
    }
    suspend fun getMyFollowedEvents():List<Events>{
        val idUser = getCurrentUserId()

        val followed = supabase.from("followed_events").select {
            filter {
                eq("id_user",idUser)
            }
        }.decodeList<FollowedEvent>()

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
        }.decodeList<FollowedEvent>()
            .map { it.id_event }
            .toSet()
    }
}