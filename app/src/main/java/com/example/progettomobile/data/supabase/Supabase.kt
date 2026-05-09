package com.example.progettomobile.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

fun Supabase(): SupabaseClient {
    val supabase = createSupabaseClient(
        supabaseUrl = "https://prpayepycjynbdwgjzll.supabase.co",
        supabaseKey = "sb_publishable_3Q0eMJkptDpyK4d-94iVKw_CKLZVrg4"
    ){
        install(Auth){
            host = "login-callback"
            scheme = "it.supabase.remembermy"
            flowType = FlowType.PKCE
        }
        install(Postgrest)
        install(Realtime)
    }
    return supabase;
}