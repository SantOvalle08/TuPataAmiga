package co.edu.uniqundio.tupataamiga.features.dashboard.navigation

sealed class DashboardRoutes(val route: String) {
    object Feed : DashboardRoutes("dashboard_feed")
    object Create : DashboardRoutes("dashboard_create")
}
