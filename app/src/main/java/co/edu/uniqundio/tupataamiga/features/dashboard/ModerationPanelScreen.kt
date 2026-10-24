package co.edu.uniqundio.tupataamiga.features.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class PendingModerationModel(
    val id: String,
    val titulo: String,
    val autor: String,
    val tiempo: String,
    val estado: String,
    val resumenIa: String,
    val badgeBg: Color,
    val badgeFg: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModerationPanelScreen(
    onBack: () -> Unit = {}
) {
    val brandGreen = Color(0xFF0F4C25)
    val fieldBg = Color(0xFFE8F5E9)

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var pendingPosts by remember {
        mutableStateOf(
            listOf(
                PendingModerationModel(
                    id = "1",
                    titulo = "Perrito mestizo para adopción",
                    autor = "por Pedro M.",
                    tiempo = "hace 20 min",
                    estado = "Adopción",
                    resumenIa = "Publicación legítima. Descripción coherente con las fotos. Acción recomendada: Verificar.",
                    badgeBg = Color(0xFFE8F5E9),
                    badgeFg = Color(0xFF2E7D32)
                ),
                PendingModerationModel(
                    id = "2",
                    titulo = "Gata encontrada en el parque",
                    autor = "por Sofía R.",
                    tiempo = "hace 45 min",
                    estado = "Encontrados",
                    resumenIa = "Foto clara de la mascota. Sin coincidencias de duplicidad previa en la zona. Recomendado: Verificar.",
                    badgeBg = Color(0xFFE3F2FD),
                    badgeFg = Color(0xFF1565C0)
                ),
                PendingModerationModel(
                    id = "3",
                    titulo = "Perro extraviado en el centro",
                    autor = "por Carlos H.",
                    tiempo = "hace 1 h",
                    estado = "Perdidos",
                    resumenIa = "Sugerencia de moderación: Comprobar teléfono de contacto antes de publicar.",
                    badgeBg = Color(0xFFFFEBEE),
                    badgeFg = Color(0xFFC62828)
                )
            )
        )
    }

    var selectedRejectPost by remember { mutableStateOf<PendingModerationModel?>(null) }
    var rejectReason by remember { mutableStateOf("") }
    var rejectError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color(0xFFF6FBF7)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF6FBF7))
        ) {
            // Header Banner Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                    .background(brandGreen)
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Volver",
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = "PANEL DE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.8f),
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Moderación",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    color = Color.White
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Perfil Moderador",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 3 Metric Cards Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ModerationMetricCard(
                            modifier = Modifier.weight(1f),
                            count = "${pendingPosts.size}",
                            label = "Pendientes",
                            bgColor = Color.White.copy(alpha = 0.18f)
                        )
                        ModerationMetricCard(
                            modifier = Modifier.weight(1f),
                            count = "12",
                            label = "Verificadas hoy",
                            bgColor = Color.White.copy(alpha = 0.18f)
                        )
                        ModerationMetricCard(
                            modifier = Modifier.weight(1f),
                            count = "2",
                            label = "Rechazadas hoy",
                            bgColor = Color.White.copy(alpha = 0.18f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title "PUBLICACIONES PENDIENTES"
            Text(
                text = "PUBLICACIONES PENDIENTES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Pending Posts List
            if (pendingPosts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = brandGreen
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "¡Todo al día!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "No hay publicaciones pendientes de moderación.",
                            fontSize = 13.sp,
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
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(pendingPosts, key = { it.id }) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Pet Image Thumbnail
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(fieldBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Pets,
                                            contentDescription = null,
                                            tint = brandGreen,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                color = item.badgeBg,
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text(
                                                    text = item.estado,
                                                    color = item.badgeFg,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                                )
                                            }

                                            Text(
                                                text = item.tiempo,
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = item.titulo,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.Black,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = item.autor,
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // AI Summary Card (Purple Card)
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.SmartToy,
                                                contentDescription = "IA",
                                                tint = Color(0xFF7B1FA2),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Resumen IA",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = Color(0xFF7B1FA2)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = item.resumenIa,
                                            fontSize = 12.sp,
                                            color = Color(0xFF4A148C),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Action Buttons Row: Verify & Reject
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            pendingPosts = pendingPosts.filter { it.id != item.id }
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Publicación verificada exitosamente")
                                            }
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(20.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = Color(0xFFE8F5E9),
                                            contentColor = Color(0xFF2E7D32)
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81C784))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Verificar",
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Verificar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            selectedRejectPost = item
                                            rejectReason = ""
                                            rejectError = null
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(20.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = Color(0xFFFFEBEE),
                                            contentColor = Color(0xFFC62828)
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE57373))
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Rechazar",
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Rechazar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Rejection Dialog with Mandatory Reason
    selectedRejectPost?.let { post ->
        AlertDialog(
            onDismissRequest = { selectedRejectPost = null },
            title = { Text("Rechazar publicación", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Escribe el motivo del rechazo para la publicación de \"${post.titulo}\":",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = {
                            rejectReason = it
                            if (rejectError != null) rejectError = null
                        },
                        placeholder = { Text("Ej: Las imágenes no corresponden a la descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = rejectError != null,
                        supportingText = {
                            rejectError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (rejectReason.isBlank()) {
                            rejectError = "Escribir un motivo es obligatorio"
                        } else {
                            pendingPosts = pendingPosts.filter { it.id != post.id }
                            selectedRejectPost = null
                            scope.launch {
                                snackbarHostState.showSnackbar("Publicación rechazada por: $rejectReason")
                            }
                        }
                    }
                ) {
                    Text("Confirmar Rechazo", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedRejectPost = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun ModerationMetricCard(
    modifier: Modifier,
    count: String,
    label: String,
    bgColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color.White.copy(alpha = 0.9f),
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
