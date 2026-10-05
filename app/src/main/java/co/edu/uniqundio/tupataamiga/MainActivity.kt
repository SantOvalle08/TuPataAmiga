package co.edu.uniqundio.tupataamiga

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import co.edu.uniqundio.tupataamiga.navigation.AppNavigation
import co.edu.uniqundio.tupataamiga.ui.theme.TuPataAmigaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TuPataAmigaTheme {
                AppNavigation()
            }
        }
    }
}
