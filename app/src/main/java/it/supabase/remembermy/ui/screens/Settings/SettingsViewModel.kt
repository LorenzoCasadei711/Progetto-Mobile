package it.supabase.remembermy.ui.screens.Settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.supabase.remembermy.ui.theme.AppPalette
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {
    val theme = repository.theme.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Theme.System
    )

    val dynamicColor = repository.dynamicColor.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        true
    )
    val palette = repository.palette.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AppPalette.Blue
    )

    fun setTheme(theme: Theme){
        viewModelScope.launch {
            repository.saveTheme(theme)
        }
    }
    fun setDynamicColor(value : Boolean){
        viewModelScope.launch {
            repository.saveDynamicColor(value)
        }
    }

    fun setPalette(palette: AppPalette){
        viewModelScope.launch{
            repository.savePalette(palette)
        }
    }
}