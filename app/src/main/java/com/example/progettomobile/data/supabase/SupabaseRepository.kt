package com.example.progettomobile.data.supabase

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

class SupabaseRepository {
    val supabase = createSupabaseClient(
        supabaseUrl = "https://prpayepycjynbdwgjzll.supabase.co",
        supabaseKey = "sb_publishable_3Q0eMJkptDpyK4d-94iVKw_CKLZVrg4"
    ){
        install(Auth)
        install(Postgrest)
        install(Realtime)
    }
    val auth = supabase.auth
}