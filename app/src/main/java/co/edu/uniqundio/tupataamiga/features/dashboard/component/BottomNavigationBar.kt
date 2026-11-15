package co.edu.uniqundio.tupataamiga.features.dashboard.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import co.edu.uniqundio.tupataamiga.features.dashboard.navigation.DashboardDestination
import co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager

@Composable
fun BottomNavigationBar(
    currentDestination: DashboardDestination,
    onDestinationSelected: (DashboardDestination) -> Unit
) {
    val brandGreen = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.brandGreenColor
    val cardBg = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.cardColor

    NavigationBar(
        containerColor = cardBg
    ) {
        DashboardDestination.entries.forEach { destination ->
            val icon = when (destination) {
                DashboardDestination.INICIO -> Icons.Default.Home
                DashboardDestination.MAPA -> Icons.Default.Map
                DashboardDestination.ALERTAS -> Icons.Default.Notifications
                DashboardDestination.PERFIL -> Icons.Default.Person
            }

            val labelText = when (destination) {
                DashboardDestination.INICIO -> LocaleManager.tr("Inicio", "Home")
                DashboardDestination.MAPA -> LocaleManager.tr("Mapa", "Map")
                DashboardDestination.ALERTAS -> LocaleManager.tr("Alertas", "Alerts")
                DashboardDestination.PERFIL -> LocaleManager.tr("Perfil", "Profile")
            }

            NavigationBarItem(
                selected = currentDestination == destination,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = labelText
                    )
                },
                label = {
                    Text(
                        text = labelText,
                        fontWeight = if (currentDestination == destination) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = brandGreen,
                    selectedTextColor = brandGreen,
                    indicatorColor = brandGreen.copy(alpha = 0.15f),
                    unselectedIconColor = Color.DarkGray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}
