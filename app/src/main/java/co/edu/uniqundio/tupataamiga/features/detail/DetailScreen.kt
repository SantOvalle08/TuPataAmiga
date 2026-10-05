package co.edu.uniqundio.tupataamiga.features.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Detalle de Mascota") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            val mascota = uiState.mascota
            if (mascota == null) {
                Text("Mascota no encontrada")
            } else {
                Text(text = mascota.nombre, style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Categoría: ${mascota.categoria}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Nivel: ${mascota.nivel}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Estado: ${mascota.estado}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = mascota.descripcion, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { viewModel.deleteMascota(onBack) }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Eliminar Publicación")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onBack) {
                Text("Volver")
            }
        }
    }
}
