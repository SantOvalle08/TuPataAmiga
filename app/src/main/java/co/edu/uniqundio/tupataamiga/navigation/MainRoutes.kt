package co.edu.uniqundio.tupataamiga.navigation

sealed class MainRoutes(val route: String) {
    object Home : MainRoutes("home")
    object Login : MainRoutes("login")
    object Register : MainRoutes("register")
    object Recovery : MainRoutes("recovery")
    object Dashboard : MainRoutes("dashboard")
    object Detail : MainRoutes("detail/{mascotaId}") {
        fun createRoute(mascotaId: String) = "detail/$mascotaId"
    }
}
