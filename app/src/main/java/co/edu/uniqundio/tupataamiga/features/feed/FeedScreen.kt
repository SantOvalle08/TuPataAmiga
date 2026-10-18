package co.edu.uniqundio.tupataamiga.features.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniqundio.tupataamiga.domain.model.EstadoPublicacion
import co.edu.uniqundio.tupataamiga.domain.model.Mascota

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    onMascotaClick: (String) -> Unit,
    onCreateClick: () -> Unit
) {
    val mascotas by viewModel.mascotas.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedEstadoFilter by remember { mutableStateOf<EstadoPublicacion?>(null) }

    val brandGreen = Color(0xFF0F4C25)

    val filteredMascotas = remember(mascotas, searchQuery, selectedEstadoFilter) {
        mascotas.filter { mascota: Mascota ->
            val matchesQuery = searchQuery.isBlank() ||
                    mascota.nombre.contains(searchQuery, ignoreCase = true) ||
                    mascota.descripcion.contains(searchQuery, ignoreCase = true)

            val matchesFilter = selectedEstadoFilter == null || mascota.estado == selectedEstadoFilter

            matchesQuery && matchesFilter
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mascotas TuPataAmiga",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = brandGreen,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateClick,
                containerColor = brandGreen,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = "Crear publicación") },
                text = { Text("Publicar", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF6FBF7))
        ) {
            // Buscador
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar mascota por nombre o descripción...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = brandGreen,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            // Filtros de Estado (Chips)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedEstadoFilter == null,
                        onClick = { selectedEstadoFilter = null },
                        label = { Text("Todos") }
                    )
                }
                items(EstadoPublicacion.entries.toTypedArray()) { estado ->
                    FilterChip(
                        selected = selectedEstadoFilter == estado,
                        onClick = { selectedEstadoFilter = if (selectedEstadoFilter == estado) null else estado },
                        label = { Text(estado.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Lista de mascotas o estado vacío
            if (filteredMascotas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No se encontraron mascotas",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredMascotas, key = { it.id }) { mascota ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onMascotaClick(mascota.id) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(brandGreen.copy(alpha = 0.1f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Pets,
                                                contentDescription = null,
                                                tint = brandGreen,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = mascota.nombre,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                            Text(
                                                text = "Categoría: ${mascota.categoria.name}",
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }

                                    // Badge de Estado
                                    val (badgeBg, badgeFg) = when (mascota.estado) {
                                        EstadoPublicacion.PERDIDO -> Color(0xFFFFEBEE) to Color(0xFFC62828)
                                        EstadoPublicacion.ADOPCION -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                                        EstadoPublicacion.ENCONTRADO -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
                                    }

                                    Surface(
                                        color = badgeBg,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = mascota.estado.name,
                                            color = badgeFg,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = mascota.descripcion,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.DarkGray,
                                    maxLines = 3
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
