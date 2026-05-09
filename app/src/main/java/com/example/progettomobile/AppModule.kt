package com.example.progettomobile

import com.example.progettomobile.data.supabase.Supabase
import com.example.progettomobile.data.supabase.SupabaseAuth
import com.example.progettomobile.ui.screens.access.AccessViewModel
import io.github.jan.supabase.SupabaseClient
import org.koin.dsl.module

val supabaseModule = module {
    single<SupabaseClient> { Supabase() }
    single<SupabaseAuth> { SupabaseAuth(get()) }
}

val viewModel = module {
    single { AccessViewModel(get()) }
}