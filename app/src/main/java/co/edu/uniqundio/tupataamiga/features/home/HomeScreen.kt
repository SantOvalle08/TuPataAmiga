package co.edu.uniqundio.tupataamiga.features.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onNavigateLogin: () -> Unit,
    onNavigateRegister: () -> Unit,
    onNavigateFeed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Tu Pata Amiga", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Encuentra, adopta y ayuda a las mascotas", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onNavigateFeed, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Ver Feed")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateLogin, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Iniciar Sesión")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateRegister, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Registrarse")
        }
    }
}
