package it.supabase.remembermy

import it.supabase.remembermy.data.supabase.SupabaseAuth
import it.supabase.remembermy.data.supabase.SupabaseData
import it.supabase.remembermy.data.supabase.supabase
import it.supabase.remembermy.ui.screens.auth.AccessViewModel
import it.supabase.remembermy.ui.screens.profile.ProfileViewModel
import io.github.jan.supabase.SupabaseClient
import it.supabase.remembermy.ui.screens.Home.HomeViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import it.supabase.remembermy.data.repository.OSMDataSource
import it.supabase.remembermy.ui.screens.Map.MapViewModel
import it.supabase.remembermy.ui.screens.Search.SearchViewModel
import it.supabase.remembermy.ui.screens.Settings.SettingsRepository
import it.supabase.remembermy.ui.screens.Settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import it.supabase.remembermy.ui.screens.camera.CameraViewModel
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val supabaseModule = module {
    single<SupabaseClient> { supabase }
    single<SupabaseAuth> { SupabaseAuth(get()) }
    single<SupabaseData> { SupabaseData(get(),androidContext().contentResolver) }
}

val viewModule = module {
    viewModel { AccessViewModel(get()) }
    viewModel { ProfileViewModel(get() ) }
    viewModel { MapViewModel(get()) }
    viewModel { CameraViewModel(get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { SearchViewModel(get(), get()) }
    single { SettingsRepository(androidContext()) }
    viewModel { SettingsViewModel(get()) }
}

val httpModule = module {
    single {
        HttpClient {
            defaultRequest {
                headers.append(
                    HttpHeaders.UserAgent,
                    "RememberMy/1.0 (it.supabase.remembermy; remembermy-app)"
                )
            }

            install(ContentNegotiation){
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }
    single { OSMDataSource(get()) }
}
