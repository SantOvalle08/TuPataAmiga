package co.edu.uniqundio.tupataamiga.features.dashboard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import co.edu.uniqundio.tupataamiga.features.create.CreateScreen
import co.edu.uniqundio.tupataamiga.features.create.CreateViewModel
import co.edu.uniqundio.tupataamiga.features.feed.FeedScreen
import co.edu.uniqundio.tupataamiga.features.feed.FeedViewModel

@Composable
fun DashboardNavigation(
    navController: NavHostController,
    feedViewModel: FeedViewModel,
    createViewModel: CreateViewModel,
    onMascotaClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = DashboardRoutes.Feed.route,
        modifier = modifier
    ) {
        composable(DashboardRoutes.Feed.route) {
            FeedScreen(
                viewModel = feedViewModel,
                onMascotaClick = onMascotaClick,
                onCreateClick = { navController.navigate(DashboardRoutes.Create.route) }
            )
        }
        composable(DashboardRoutes.Create.route) {
            CreateScreen(
                viewModel = createViewModel,
                onCreated = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
