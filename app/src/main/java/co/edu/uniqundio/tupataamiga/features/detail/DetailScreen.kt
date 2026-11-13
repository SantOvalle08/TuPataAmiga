package co.edu.uniqundio.tupataamiga.features.detail

import android.preference.PreferenceManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import co.edu.uniqundio.tupataamiga.ui.theme.abrirComoLlegar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(value = false) }
    var isBookmarked by remember { mutableStateOf(value = false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val brandGreen = Color(0xFF0F4C25)
    val fieldBg = Color(0xFFE8F5E9)

    val mascota = uiState.mascota

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF6FBF7),
    ) { padding ->
        if (mascota == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = brandGreen)
            }
        } else {
            // Coordenadas y dirección (si tu modelo Mascota ya tiene lat/lng/ubicacion, úsalos aquí)
            val (direccionMascota, latMascota, lngMascota) = remember(mascota.id) {
                when (mascota.id) {
                    "1" -> Triple("El Bosque, Armenia, Quindío", 4.5389, -75.6781)
                    "2" -> Triple("Centro, Armenia, Quindío", 4.5338, -75.6811)
                    "3" -> Triple("Plaza de Bolívar, Armenia", 4.5321, -75.6834)
                    "4" -> Triple("La Castellana, Armenia", 4.5456, -75.6723)
                    else -> Triple("Armenia, Quindío", 4.5375, -75.6780)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                // Top Media Banner Container with Overlaid Action Icons
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(fieldBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = null,
                        tint = brandGreen,
                        modifier = Modifier.size(80.dp),
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .align(Alignment.TopStart),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.9f)),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.Black,
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Enlace copiado al portapapeles")
                                    }
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.9f)),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Compartir",
                                    tint = Color.Black,
                                )
                            }
                            IconButton(
                                onClick = {
                                    isBookmarked = !isBookmarked
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (isBookmarked) "Guardado en tus favoritos" else "Removido de tus favoritos",
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.9f)),
                            ) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Guardar",
                                    tint = if (isBookmarked) brandGreen else Color.Black,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Badges Line: Categoria & Estado
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val (badgeBg, badgeFg) = when (mascota.categoria) {
                        co.edu.uniqundio.tupataamiga.domain.model.Categoria.PERDIDO -> Color(0xFFFFEBEE) to Color(0xFFC62828)
                        co.edu.uniqundio.tupataamiga.domain.model.Categoria.ADOPCION -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                        co.edu.uniqundio.tupataamiga.domain.model.Categoria.ENCONTRADO -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
                        co.edu.uniqundio.tupataamiga.domain.model.Categoria.HOGAR_TEMPORAL -> Color(0xFFFFF8E1) to Color(0xFFF57F17)
                        co.edu.uniqundio.tupataamiga.domain.model.Categoria.JORNADA_VETERINARIA -> Color(0xFFF3E5F5) to Color(0xFF7B1FA2)
                    }

                    Surface(
                        color = badgeBg,
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(
                            text = mascota.categoria.name,
                            color = badgeFg,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verificado",
                            tint = Color(0xFF1E88E5),
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Verificado",
                            color = Color(0xFF1E88E5),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Title
                Text(
                    text = "${mascota.nombre} busca un hogar amoroso",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 20.sp,
                    ),
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Author Header Line
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(brandGreen),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = "CH", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Carlos Herrera",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                        )
                        Text(
                            text = "hace 2 horas",
                            color = Color.Gray,
                            fontSize = 11.sp,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Metadata Grid Box (2x2)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = fieldBg),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Tipo", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    text = "🐕 ${mascota.categoria.name.lowercase().replaceFirstChar { it.uppercase() }}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Raza", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    text = "Labrador",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Tamaño", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    text = "Grande",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Vacunas", fontSize = 11.sp, color = Color.Gray)
                                Text(
                                    text = "✅ Al día",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description Text
                Text(
                    text = mascota.descripcion,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 20.sp,
                    ),
                )

                Spacer(modifier = Modifier.height(18.dp))

                // NUEVA SECCIÓN: Ubicación + Mini-Mapa + Botón "Cómo llegar"
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(fieldBg),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = brandGreen,
                                        modifier = Modifier.size(20.dp),
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Ubicación reportada",
                                        fontSize = 11.sp,
                                        color = Color.Gray,
                                    )
                                    Text(
                                        text = direccionMascota,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.Black,
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    abrirComoLlegar(context, latMascota, lngMascota, mascota.nombre)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Navigation,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(15.dp),
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Cómo llegar",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Mini-mapa de vista previa
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(135.dp)
                                .clip(RoundedCornerShape(12.dp)),
                        ) {
                            AndroidView(
                                modifier = Modifier.fillMaxSize(),
                                factory = { ctx ->
                                    Configuration.getInstance().load(
                                        ctx,
                                        PreferenceManager.getDefaultSharedPreferences(ctx)
                                    )
                                    Configuration.getInstance().userAgentValue = ctx.packageName

                                    MapView(ctx).apply {
                                        setTileSource(TileSourceFactory.MAPNIK)
                                        setMultiTouchControls(false)
                                        controller.setZoom(16.0)
                                        val punto = GeoPoint(latMascota, lngMascota)
                                        controller.setCenter(punto)

                                        val pin = Marker(this).apply {
                                            position = punto
                                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                            title = mascota.nombre
                                        }
                                        overlays.add(pin)
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.height(14.dp))

                // COMENTARIOS SECTION
                Text(
                    text = "Comentarios (${mascota.comentarios.size})",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                    ),
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Add Comment TextField & Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TextField(
                        value = uiState.nuevoComentarioTexto,
                        onValueChange = viewModel::onComentarioTextChanged,
                        placeholder = { Text("Escribe un comentario...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(20.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = fieldBg,
                            unfocusedContainerColor = fieldBg,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        singleLine = true,
                    )
                    IconButton(
                        onClick = {
                            viewModel.agregarComentario {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Comentario publicado")
                                }
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(brandGreen),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Comments List
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    mascota.comentarios.forEach { comentario ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = comentario.nombreUsuario,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = brandGreen,
                                    )
                                    Text(
                                        text = comentario.fecha,
                                        fontSize = 11.sp,
                                        color = Color.Gray,
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = comentario.texto,
                                    fontSize = 13.sp,
                                    color = Color.DarkGray,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Button(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color.White,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Eliminar Publicación",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    Text("Volver al Feed", color = brandGreen, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("¿Eliminar publicación?") },
            text = { Text("Esta acción no se puede deshacer. La publicación será removida del feed.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteMascota {
                            scope.launch {
                                snackbarHostState.showSnackbar("Publicación eliminada correctamente")
                                delay(800.milliseconds)
                                onBack()
                            }
                        }
                    },
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            },
        )
    }
}