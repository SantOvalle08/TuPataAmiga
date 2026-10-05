package co.edu.uniqundio.tupataamiga.features.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    onMascotaClick: (String) -> Unit,
    onCreateClick: () -> Unit
) {
    val mascotas by viewModel.mascotas.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Feed de Mascotas") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateClick) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(mascotas) { mascota ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onMascotaClick(mascota.id) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = mascota.nombre, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Categoría: ${mascota.categoria} - Estado: ${mascota.estado}")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = mascota.descripcion, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}
