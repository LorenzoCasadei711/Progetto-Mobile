package com.example.progettomobile

import com.example.progettomobile.data.supabase.SupabaseAuth
import com.example.progettomobile.data.supabase.SupabaseData
import com.example.progettomobile.data.supabase.supabase
import com.example.progettomobile.ui.screens.access.AccessViewModel
import com.example.progettomobile.ui.screens.profile.ProfileViewModel
import com.example.progettomobile.viewModule
import io.github.jan.supabase.SupabaseClient
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
}