package co.edu.uniqundio.tupataamiga.features.dashboard.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import co.edu.uniqundio.tupataamiga.features.dashboard.navigation.DashboardDestination

@Composable
fun BottomNavigationBar(
    currentDestination: DashboardDestination,
    onDestinationSelected: (DashboardDestination) -> Unit
) {
    val brandGreen = Color(0xFF0F4C25)

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceContainer
    ) {
        DashboardDestination.entries.forEach { destination ->
            val icon = when (destination) {
                DashboardDestination.FEED -> Icons.Default.Pets
                DashboardDestination.CREATE -> Icons.Default.AddCircle
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
                label = { Text(destination.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = brandGreen,
                    selectedTextColor = brandGreen,
                    indicatorColor = brandGreen.copy(alpha = 0.15f)
                )
            )
        }
    }
}
