package it.supabase.remembermy.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.progettomobile.composable.BottomAppBar
import it.supabase.remembermy.composable.TopAppBar

 enum class Theme { Light, Dark, System}
@Composable
fun SettingsScreen(navController : NavHostController,
                   selectedTheme : Theme,
                   onThemeChange: (Theme) -> Unit,
                   dynamicColor : Boolean,
                   onDynamicColorChange: (Boolean) -> Unit
) {

    Scaffold(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier.fillMaxSize(),
        bottomBar = {BottomAppBar(navController)},
        topBar = {TopAppBar("AppMobile",navController)}

    ) { innerPadding ->
        Column(Modifier.padding(innerPadding).selectableGroup()) {
            Text(
                "Theme",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            Theme.entries.forEach { theme ->
                RadioListItem(
                    label = theme.toString(),
                    selected = (theme == selectedTheme),
                    onClick = {onThemeChange(theme)}
                )
            }
            Text(
                "Color Palette",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            listOf(true, false) .forEach { dynamicColorEnabled ->
                RadioListItem(
                    label = if (dynamicColorEnabled) "System colors" else "Custom colors",
                    selected = (dynamicColorEnabled == dynamicColor),
                    onClick = {onDynamicColorChange(dynamicColorEnabled)}
                )
            }
        }
    }
}

@Composable
fun RadioListItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
