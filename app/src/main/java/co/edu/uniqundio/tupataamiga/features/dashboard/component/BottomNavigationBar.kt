package co.edu.uniqundio.tupataamiga.features.dashboard.component

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import co.edu.uniqundio.tupataamiga.features.dashboard.navigation.DashboardDestination

@Composable
fun BottomNavigationBar(
    currentDestination: DashboardDestination,
    onDestinationSelected: (DashboardDestination) -> Unit
) {
    NavigationBar {
        DashboardDestination.values().forEach { destination ->
            NavigationBarItem(
                selected = currentDestination == destination,
                onClick = { onDestinationSelected(destination) },
                icon = { Text(destination.title.take(1)) },
                label = { Text(destination.title) }
            )
        }
    }
}
