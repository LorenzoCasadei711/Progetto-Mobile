package ui.screens.theme


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ui.screens.SettingsScreen
import ui.screens.Theme


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