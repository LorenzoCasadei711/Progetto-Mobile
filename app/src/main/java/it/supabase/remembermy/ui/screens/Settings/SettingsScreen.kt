package it.supabase.remembermy.ui.screens.Settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.progettomobile.composable.BottomAppBar
import com.example.progettomobile.composable.NavigationRoute
import it.supabase.remembermy.composable.TopAppBar
import it.supabase.remembermy.ui.theme.AppPalette
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color


enum class Theme { Light, Dark, System}
@Composable
fun SettingsScreen(navController : NavHostController,
                   selectedTheme : Theme,
                   onThemeChange: (Theme) -> Unit,
                   dynamicColor : Boolean,
                   onDynamicColorChange: (Boolean) -> Unit,
                   selectedPalette: AppPalette,
                   onPaletteChange: (AppPalette) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier.fillMaxSize(),
        bottomBar = {BottomAppBar(navController)},
        topBar = {TopAppBar("Impostazioni",navController)}

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
            if(!dynamicColor) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PaletteButton(
                        color = Color(0xFF4F67A5),
                        selected = selectedPalette == AppPalette.Blue,
                        onClick = {onPaletteChange(AppPalette.Blue)}
                    )

                    PaletteButton(
                        color = Color(0xFF2E7D32),
                        selected = selectedPalette == AppPalette.Green,
                        onClick = {onPaletteChange(AppPalette.Green)}
                    )

                    PaletteButton(
                        color = Color(0xFF6750A4),
                        selected = selectedPalette == AppPalette.Purple,
                        onClick = {onPaletteChange(AppPalette.Purple)}
                    )

                    PaletteButton(
                        color = Color(0xFFE07A00),
                        selected = selectedPalette == AppPalette.Orange,
                        onClick = {onPaletteChange(AppPalette.Orange)}
                    )

                    PaletteButton(
                        color = Color(0xFFC62828),
                        selected = selectedPalette == AppPalette.Red,
                        onClick = {onPaletteChange(AppPalette.Red)}
                    )
                }
            }
            Column() {
                Text(
                    "Profilo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                )
                Button(
                    onClick = {
                        navController.navigate(NavigationRoute.ChangeInfo)
                    },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text("Modifica informazioni profilo.")
                }
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

@Composable
fun PaletteButton(
    color: Color,
    selected : Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(if(selected) 48.dp else 40.dp)
            .clip(CircleShape)
            .background(color)
            .clickable {onClick()}
    )
}
