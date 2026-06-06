package it.supabase.remembermy.data.supabase

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage



val supabase = createSupabaseClient(
    supabaseUrl = "https://prpayepycjynbdwgjzll.supabase.co",
    supabaseKey = "sb_publishable_3Q0eMJkptDpyK4d-94iVKw_CKLZVrg4"
) {
    install(Auth) {
        scheme = "it.supabase.remembermy"
        host = "login-callback"
        flowType = FlowType.PKCE
    }
    install(Postgrest)
    install(Realtime)
    install(Storage)
}

