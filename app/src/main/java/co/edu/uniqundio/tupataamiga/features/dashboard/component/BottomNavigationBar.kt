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

@Composable
fun BottomNavigationBar(
    currentDestination: DashboardDestination,
    onDestinationSelected: (DashboardDestination) -> Unit
) {
    val brandGreen = Color(0xFF0F4C25)
    val indicatorBg = Color(0xFFE8F5E9)

    NavigationBar(
        containerColor = Color.White
    ) {
        DashboardDestination.entries.forEach { destination ->
            val icon = when (destination) {
                DashboardDestination.INICIO -> Icons.Default.Home
                DashboardDestination.MAPA -> Icons.Default.Map
                DashboardDestination.ALERTAS -> Icons.Default.Notifications
                DashboardDestination.PERFIL -> Icons.Default.Person
            }

            NavigationBarItem(
                selected = currentDestination == destination,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = destination.title
                    )
                },
                label = {
                    Text(
                        text = destination.title,
                        fontWeight = if (currentDestination == destination) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 12.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = brandGreen,
                    selectedTextColor = brandGreen,
                    indicatorColor = indicatorBg,
                    unselectedIconColor = Color.DarkGray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}
