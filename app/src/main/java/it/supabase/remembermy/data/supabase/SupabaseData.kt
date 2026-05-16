package it.supabase.remembermy.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class SupabaseData(val supabase: SupabaseClient) {

    suspend fun getUser() = supabase.from("profiles").select{
           filter {
               eq("id_user", supabase.auth.retrieveUserForCurrentSession().id)
           }
    }.decodeAs<Profiles>()

    suspend fun getListEvents() = supabase.from("events").select {
        filter {
            eq("id_user", supabase.auth.retrieveUserForCurrentSession().id)
        }
    }.decodeList<Events>()

    suspend fun logout() = supabase.auth.signOut()

}