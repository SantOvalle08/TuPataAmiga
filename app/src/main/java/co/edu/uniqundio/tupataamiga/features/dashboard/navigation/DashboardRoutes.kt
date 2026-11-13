package co.edu.uniqundio.tupataamiga.features.dashboard.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class DashboardRoutes(val route: String) {
    @Serializable
    data object Feed : DashboardRoutes("dashboard_feed")

    @Serializable
    data object Create : DashboardRoutes("dashboard_create")

    @Serializable
    data object Map : DashboardRoutes("dashboard_map")

    @Serializable
    data object Alerts : DashboardRoutes("dashboard_alerts")

    @Serializable
    data object Profile : DashboardRoutes("dashboard_profile")
}
