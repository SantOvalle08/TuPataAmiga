package co.edu.uniqundio.tupataamiga.features.dashboard.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import co.edu.uniqundio.tupataamiga.features.create.CreateScreen
import co.edu.uniqundio.tupataamiga.features.create.CreateViewModel
import co.edu.uniqundio.tupataamiga.features.dashboard.AlertsScreen
import co.edu.uniqundio.tupataamiga.features.dashboard.ProfileScreen
import co.edu.uniqundio.tupataamiga.features.feed.FeedScreen
import co.edu.uniqundio.tupataamiga.features.feed.FeedViewModel

@Composable
fun DashboardNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    feedViewModel: FeedViewModel,
    createViewModel: CreateViewModel,
    onMascotaClick: (String) -> Unit,
    onLogout: () -> Unit = {},
    onDestinationChange: (DashboardDestination) -> Unit = {},
) {
    val brandGreen = Color(0xFF0F4C25)

    NavHost(
        navController = navController,
        startDestination = DashboardRoutes.Feed.route,
        modifier = modifier,
    ) {
        composable(DashboardRoutes.Feed.route) {
            FeedScreen(
                viewModel = feedViewModel,
                onMascotaClick = onMascotaClick,
                onCreateClick = { navController.navigate(DashboardRoutes.Create.route) },
                onAlertsClick = {
                    onDestinationChange(DashboardDestination.ALERTAS)
                    navController.navigate(DashboardRoutes.Alerts.route) {
                        popUpTo(DashboardRoutes.Feed.route)
                    }
                },
            )
        }
        composable(DashboardRoutes.Create.route) {
            CreateScreen(
                viewModel = createViewModel,
                onCreated = { navController.popBackStack() },
                onBack = { navController.popBackStack() },
            )
        }
        composable(DashboardRoutes.Map.route) {
            PlaceholderTabScreen(
                title = "Mapa de Mascotas",
                icon = Icons.Default.Map,
                message = "Explora las publicaciones cercanas en Armenia, Quindío.",
                brandGreen = brandGreen,
            )
        }
        composable(DashboardRoutes.Alerts.route) {
            AlertsScreen()
        }
        composable(DashboardRoutes.Profile.route) {
            ProfileScreen(
                onLogoutClick = onLogout,
            )
        }
    }
}

@Composable
private fun PlaceholderTabScreen(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    message: String,
    brandGreen: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6FBF7))
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = brandGreen,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray,
                        fontSize = 14.sp,
                    ),
                )
            }
        }
    }
}
