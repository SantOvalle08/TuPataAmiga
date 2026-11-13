package co.edu.uniqundio.tupataamiga.ui.theme
import android.content.Context
import android.content.Intent
import android.net.Uri

fun abrirComoLlegar(context: Context, lat: Double, lng: Double, etiqueta: String) {
    val gmmIntentUri = Uri.parse("google.navigation:q=$lat,$lng")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
        setPackage("com.google.android.apps.maps")
    }

    if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
    } else {
        val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng")
        val webIntent = Intent(Intent.ACTION_VIEW, webUri)
        context.startActivity(webIntent)
    }
}