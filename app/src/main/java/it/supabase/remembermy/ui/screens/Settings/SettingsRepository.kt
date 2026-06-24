package it.supabase.remembermy.ui.screens.Settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import it.supabase.remembermy.ui.theme.AppPalette
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class SettingsRepository(
    private val context: Context
) {
    private val themeKey = stringPreferencesKey("theme")
    private val dynamicColorKey = booleanPreferencesKey("dynamic_color")
    private val paletteKey = stringPreferencesKey("palette")

    val theme = context.dataStore.data.map { prefs ->
        Theme.valueOf(prefs[themeKey]?:Theme.System.name)
    }
    val palette = context.dataStore.data.map {prefs ->
        AppPalette.valueOf(prefs[paletteKey]?: AppPalette.Blue.name)
    }
    val dynamicColor = context.dataStore.data.map { prefs ->
        prefs[dynamicColorKey] ?: true
    }

    suspend fun saveTheme(theme: Theme){
        context.dataStore.edit { prefs ->
            prefs[themeKey] = theme.name
        }
    }

    suspend fun saveDynamicColor(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[dynamicColorKey] = value
        }
    }

    suspend fun savePalette(palette : AppPalette){
        context.dataStore.edit { prefs ->
            prefs[paletteKey] = palette.name
        }
    }
}