package co.edu.uniqundio.tupataamiga.features.feed

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniqundio.tupataamiga.domain.model.Categoria
import co.edu.uniqundio.tupataamiga.domain.model.EstadoPublicacion
import co.edu.uniqundio.tupataamiga.domain.model.Mascota
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel,
    onMascotaClick: (String) -> Unit,
    onCreateClick: () -> Unit,
    onAlertsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
) {
    val mascotas by viewModel.mascotas.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var searchQuery by remember { mutableStateOf("") }
    var selectedEstadoFilter by remember { mutableStateOf<EstadoPublicacion?>(null) }

    val backgroundColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.backgroundColor
    val cardBg = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.cardColor
    val mainTextColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.textColor
    val subTextColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.subtextColor
    val brandGreen = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.brandGreenColor
    val fieldBg = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.fieldColor

    val filteredMascotas = remember(mascotas, searchQuery, selectedEstadoFilter) {
        mascotas.filter { mascota: Mascota ->
            val matchesQuery = searchQuery.isBlank() ||
                    mascota.nombre.contains(searchQuery, ignoreCase = true) ||
                    mascota.descripcion.contains(searchQuery, ignoreCase = true) ||
                    mascota.raza.contains(searchQuery, ignoreCase = true) ||
                    mascota.ubicacion.contains(searchQuery, ignoreCase = true)

            val matchesFilter = selectedEstadoFilter == null || mascota.estado == selectedEstadoFilter

            matchesQuery && matchesFilter
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PataAmiga",
                        fontWeight = FontWeight.Bold,
                        color = mainTextColor,
                        fontSize = 22.sp
                    )
                },
                actions = {
                    IconButton(onClick = onAlertsClick) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notificaciones",
                            tint = brandGreen
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(brandGreen),
                        contentAlignment = Alignment.Center,
                    ) {
                        androidx.compose.material3.Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(onClick = onProfileClick),
                            color = Color.Transparent,
                        ) {
                            if (!currentUser?.fotoUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = currentUser?.fotoUrl,
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                )
                            } else {
                                Text(
                                    text = currentUser?.nombre
                                        ?.split(" ")
                                        ?.mapNotNull { it.firstOrNull()?.uppercase() }
                                        ?.take(2)
                                        ?.joinToString("")
                                        ?: "MG",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateClick,
                containerColor = brandGreen,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = "Crear publicación") },
                text = { Text(co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Publicar", "Post"), fontWeight = FontWeight.Bold) },
                shape = RoundedCornerShape(24.dp)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(backgroundColor)
        ) {
            // Buscador redondo
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                placeholder = { Text(co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Buscar mascotas...", "Search pets...")) },
                leadingIcon = { Icon(Icons.Default.Pets, contentDescription = "Buscar", tint = brandGreen) },
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = brandGreen,
                    unfocusedContainerColor = fieldBg,
                    focusedContainerColor = fieldBg,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            // Categorías (Filter Chips)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedEstadoFilter == null,
                        onClick = { selectedEstadoFilter = null },
                        label = { Text(co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Todos", "All")) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = brandGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                items(EstadoPublicacion.entries.toTypedArray()) { estado ->
                    val estadoNombre = when (estado) {
                        EstadoPublicacion.PENDIENTE -> co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Pendiente", "Pending")
                        EstadoPublicacion.APROBADO -> co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Aprobado", "Approved")
                        EstadoPublicacion.RECHAZADO -> co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Rechazado", "Rejected")
                        EstadoPublicacion.FINALIZADO -> co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Finalizado", "Resolved")
                    }
                    FilterChip(
                        selected = selectedEstadoFilter == estado,
                        onClick = { selectedEstadoFilter = if (selectedEstadoFilter == estado) null else estado },
                        label = { Text(estadoNombre) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = brandGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Ubicación Subtítulo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = brandGreen,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Armenia, Quindío",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = brandGreen
                )
                Text(
                    text = " · ${filteredMascotas.size} resultados",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Lista de Mascotas (Feed)
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
                            text = "No se encontraron publicaciones",
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
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredMascotas, key = { it.id }) { mascota ->
                        val isLiked = currentUser?.id in mascota.interesadosIds
                        val likesCount = mascota.interesadosIds.size

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onMascotaClick(mascota.id) },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Foto real de la mascota
                                    Box(
                                        modifier = Modifier
                                            .size(84.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(fieldBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (mascota.imagenUrl.isNotBlank()) {
                                            AsyncImage(
                                                model = mascota.imagenUrl,
                                                contentDescription = mascota.nombre,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Pets,
                                                contentDescription = null,
                                                tint = brandGreen,
                                                modifier = Modifier.size(40.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val (badgeBg, badgeFg) = when (mascota.categoria) {
                                                Categoria.PERDIDO -> Color(0xFFFFEBEE) to Color(0xFFC62828)
                                                Categoria.ADOPCION -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                                                Categoria.ENCONTRADO -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
                                                Categoria.HOGAR_TEMPORAL -> Color(0xFFFFF8E1) to Color(0xFFF57F17)
                                                Categoria.JORNADA_VETERINARIA -> Color(0xFFF3E5F5) to Color(0xFF7B1FA2)
                                            }

                                            val catTexto = when (mascota.categoria) {
                                                Categoria.ADOPCION -> co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("ADOPCION", "ADOPTION")
                                                Categoria.PERDIDO -> co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("PERDIDO", "LOST")
                                                Categoria.ENCONTRADO -> co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("ENCONTRADO", "FOUND")
                                                Categoria.HOGAR_TEMPORAL -> co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("HOGAR TEMPORAL", "FOSTER HOME")
                                                Categoria.JORNADA_VETERINARIA -> co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("JORNADA VET", "VET DRIVE")
                                            }

                                            Surface(
                                                color = badgeBg,
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text(
                                                    text = catTexto,
                                                    color = badgeFg,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                                )
                                            }

                                            if (mascota.estado == EstadoPublicacion.APROBADO) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Verificado",
                                                    tint = Color(0xFF1E88E5),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            } else if (mascota.estado == EstadoPublicacion.PENDIENTE) {
                                                Icon(
                                                    imageVector = Icons.Default.HourglassEmpty,
                                                    contentDescription = "Pendiente",
                                                    tint = Color(0xFFF57F17),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = mascota.nombre,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(2.dp))

                                        val tamanoTexto = mascota.tamano.name.lowercase().replaceFirstChar { it.uppercase() }
                                        Text(
                                            text = "${mascota.raza} · $tamanoTexto",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.LocationOn,
                                                contentDescription = null,
                                                tint = Color.Gray,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(
                                                text = mascota.ubicacion.ifBlank { "Armenia, Quindío" },
                                                fontSize = 11.sp,
                                                color = Color.Gray,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                                Spacer(modifier = Modifier.height(8.dp))

                                // Footer con Likes y Botón de Acción
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.clickable {
                                            viewModel.toggleInteres(mascota)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Me gusta",
                                            tint = if (isLiked) Color(0xFFE53935) else Color.Gray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "$likesCount", fontSize = 12.sp, color = Color.DarkGray)
                                    }

                                    val textoBoton = when (mascota.categoria) {
                                        Categoria.ADOPCION -> co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Adoptar", "Adopt")
                                        Categoria.PERDIDO, Categoria.ENCONTRADO -> co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Reportar", "Report")
                                        else -> co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Ver detalle", "View Detail")
                                    }

                                    Surface(
                                        color = fieldBg,
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.clickable {
                                            val requiereContacto = mascota.categoria == Categoria.ADOPCION ||
                                                    mascota.categoria == Categoria.PERDIDO ||
                                                    mascota.categoria == Categoria.ENCONTRADO
                                            if (requiereContacto) {
                                                scope.launch {
                                                    val correo = runCatching {
                                                        viewModel.obtenerCorreoPropietario(mascota)
                                                    }.getOrNull()
                                                    if (!correo.isNullOrBlank() && correo.contains("@")) {
                                                        context.startActivity(
                                                            Intent(
                                                                Intent.ACTION_SENDTO,
                                                                Uri.parse("mailto:$correo"),
                                                            ).apply {
                                                                putExtra(
                                                                    Intent.EXTRA_SUBJECT,
                                                                    "Contacto sobre la publicación: ${mascota.nombre}",
                                                                )
                                                            },
                                                        )
                                                    } else {
                                                        snackbarHostState.showSnackbar(
                                                            "No hay un correo de contacto disponible para esta publicación",
                                                        )
                                                    }
                                                }
                                            } else {
                                                onMascotaClick(mascota.id)
                                            }
                                        }
                                    ) {
                                        Text(
                                            text = textoBoton,
                                            color = brandGreen,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}