package co.edu.uniqundio.tupataamiga.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class MainRoutes(val route: String) {
    @Serializable
    data object Home : MainRoutes("home")

    @Serializable
    data object Login : MainRoutes("login")

    @Serializable
    data object Register : MainRoutes("register")

    @Serializable
    data object Recovery : MainRoutes("recovery")

    @Serializable
    data object Dashboard : MainRoutes("dashboard")

    @Serializable
    data class Detail(val mascotaId: String) : MainRoutes("detail/{mascotaId}") {
        companion object {
            fun createRoute(mascotaId: String) = "detail/$mascotaId"
        }
    }
}
