package it.supabase.remembermy.ui.screens.theme


import androidx.lifecycle.ViewModel
import it.supabase.remembermy.ui.screens.Settings.Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


data class ThemeState(
    val theme : Theme,
    val dynamicColor : Boolean
)
data class ThemeActions(
    val setTheme : (Theme) -> Unit,
    val setDynamicColor: (Boolean) -> Unit
)

class ThemeViewModel: ViewModel(){
    private val _state =
        MutableStateFlow(ThemeState(Theme.System,true))
    val state = _state.asStateFlow()
    val actions = ThemeActions(
        setTheme = { theme ->
            _state.update { it.copy(theme = theme) }
        },
        setDynamicColor = {enabled ->
            _state.update { it.copy(dynamicColor = enabled) }
        }
    )
}