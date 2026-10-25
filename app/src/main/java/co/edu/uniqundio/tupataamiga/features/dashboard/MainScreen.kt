package co.edu.uniqundio.tupataamiga.features.dashboard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import co.edu.uniqundio.tupataamiga.features.create.CreateViewModel
import co.edu.uniqundio.tupataamiga.features.dashboard.component.BottomNavigationBar
import co.edu.uniqundio.tupataamiga.features.dashboard.navigation.DashboardDestination
import co.edu.uniqundio.tupataamiga.features.dashboard.navigation.DashboardNavigation
import co.edu.uniqundio.tupataamiga.features.dashboard.navigation.DashboardRoutes
import co.edu.uniqundio.tupataamiga.features.feed.FeedViewModel

@Composable
fun MainScreen(
    onMascotaClick: (String) -> Unit,
    onLogout: () -> Unit = {}
) {
    val navController = rememberNavController()
    var currentDestination by remember { mutableStateOf(DashboardDestination.INICIO) }
    val feedViewModel: FeedViewModel = hiltViewModel()
    val createViewModel: CreateViewModel = hiltViewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(
                currentDestination = currentDestination,
                onDestinationSelected = { destination ->
                    currentDestination = destination
                    when (destination) {
                        DashboardDestination.INICIO -> navController.navigate(DashboardRoutes.Feed.route) {
                            popUpTo(DashboardRoutes.Feed.route) { inclusive = true }
                        }
                        DashboardDestination.MAPA -> navController.navigate(DashboardRoutes.Map.route) {
                            popUpTo(DashboardRoutes.Feed.route)
                        }
                        DashboardDestination.ALERTAS -> navController.navigate(DashboardRoutes.Alerts.route) {
                            popUpTo(DashboardRoutes.Feed.route)
                        }
                        DashboardDestination.PERFIL -> navController.navigate(DashboardRoutes.Profile.route) {
                            popUpTo(DashboardRoutes.Feed.route)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        DashboardNavigation(
            navController = navController,
            feedViewModel = feedViewModel,
            createViewModel = createViewModel,
            onMascotaClick = onMascotaClick,
            onLogout = onLogout,
            onDestinationChange = { destination -> currentDestination = destination },
            modifier = Modifier.padding(innerPadding)
        )
    }
}
