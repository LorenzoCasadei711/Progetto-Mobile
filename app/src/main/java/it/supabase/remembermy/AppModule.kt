package it.supabase.remembermy

import it.supabase.remembermy.data.supabase.SupabaseAuth
import it.supabase.remembermy.data.supabase.SupabaseData
import it.supabase.remembermy.data.supabase.supabase
import it.supabase.remembermy.ui.screens.auth.AccessViewModel
import it.supabase.remembermy.ui.screens.profile.ProfileViewModel
import io.github.jan.supabase.SupabaseClient
import it.supabase.remembermy.ui.screens.Map.MapViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val supabaseModule = module {
    single<SupabaseClient> { supabase }
    single<SupabaseAuth> { SupabaseAuth(get()) }
    single<SupabaseData> { SupabaseData(get()) }
}

val viewModule = module {
    viewModel { AccessViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { MapViewModel(get()) }
}