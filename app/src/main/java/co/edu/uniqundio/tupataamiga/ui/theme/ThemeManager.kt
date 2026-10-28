package co.edu.uniqundio.tupataamiga.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

object ThemeManager {
    var isDarkTheme = mutableStateOf(false)

    val backgroundColor: Color
        @Composable
        get() = if (isDarkTheme.value) Color(0xFF121212) else Color(0xFFF6FBF7)

    val cardColor: Color
        @Composable
        get() = if (isDarkTheme.value) Color(0xFF1E1E1E) else Color.White

    val textColor: Color
        @Composable
        get() = if (isDarkTheme.value) Color.White else Color.Black

    val subtextColor: Color
        @Composable
        get() = if (isDarkTheme.value) Color(0xFFB0B0B0) else Color.Gray

    val fieldColor: Color
        @Composable
        get() = if (isDarkTheme.value) Color(0xFF2C2C2C) else Color(0xFFE8F5E9)

    val brandGreenColor: Color
        @Composable
        get() = if (isDarkTheme.value) Color(0xFF81C784) else Color(0xFF0F4C25)
}
