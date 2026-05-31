package it.supabase.remembermy.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.jwt.SharedJwkCache.set
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

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
}