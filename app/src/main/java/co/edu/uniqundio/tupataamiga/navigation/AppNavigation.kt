package co.edu.uniqundio.tupataamiga.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.edu.uniqundio.tupataamiga.features.dashboard.MainScreen
import co.edu.uniqundio.tupataamiga.features.detail.DetailScreen
import co.edu.uniqundio.tupataamiga.features.detail.DetailViewModel
import co.edu.uniqundio.tupataamiga.features.home.HomeScreen
import co.edu.uniqundio.tupataamiga.features.login.LoginScreen
import co.edu.uniqundio.tupataamiga.features.login.LoginViewModel
import co.edu.uniqundio.tupataamiga.features.recovery.RecoveryScreen
import co.edu.uniqundio.tupataamiga.features.register.RegisterScreen
import co.edu.uniqundio.tupataamiga.features.register.RegisterViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = MainRoutes.Home.route) {
        composable(MainRoutes.Home.route) {
            HomeScreen(
                onNavigateLogin = { navController.navigate(MainRoutes.Login.route) },
                onNavigateRegister = { navController.navigate(MainRoutes.Register.route) },
                onNavigateFeed = { navController.navigate(MainRoutes.Dashboard.route) }
            )
        }
        composable(MainRoutes.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(MainRoutes.Dashboard.route) {
                        popUpTo(MainRoutes.Home.route)
                    }
                },
                onNavigateRecovery = { navController.navigate(MainRoutes.Recovery.route) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(MainRoutes.Register.route) {
            val viewModel: RegisterViewModel = hiltViewModel()
            RegisterScreen(
                viewModel = viewModel,
                onRegisterSuccess = {
                    navController.navigate(MainRoutes.Dashboard.route) {
                        popUpTo(MainRoutes.Home.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(MainRoutes.Recovery.route) {
            RecoveryScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(MainRoutes.Dashboard.route) {
            MainScreen(
                onMascotaClick = { mascotaId ->
                    navController.navigate(MainRoutes.Detail.createRoute(mascotaId))
                }
            )
        }
        composable(
            route = MainRoutes.Detail.route,
            arguments = listOf(navArgument("mascotaId") { type = NavType.StringType })
        ) {
            val viewModel: DetailViewModel = hiltViewModel()
            DetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
