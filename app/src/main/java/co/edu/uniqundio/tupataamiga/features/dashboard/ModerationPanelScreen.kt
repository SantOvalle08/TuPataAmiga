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
import androidx.hilt.navigation.compose.hiltViewModel
import co.edu.uniqundio.tupataamiga.domain.model.Mascota
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModerationPanelScreen(
    viewModel: ModerationViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val backgroundColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.backgroundColor
    val cardBg = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.cardColor
    val mainTextColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.textColor
    val subTextColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.subtextColor
    val brandGreen = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.brandGreenColor
    val fieldBg = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.fieldColor

    val pendingPosts by viewModel.pendingPosts.collectAsState()
    val approvedPosts by viewModel.approvedPosts.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var selectedRejectPost by remember { mutableStateOf<Mascota?>(null) }
    var rejectReason by remember { mutableStateOf("") }
    var rejectError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(backgroundColor)
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
                            count = "${approvedPosts.size}",
                            label = "Aprobadas",
                            bgColor = Color.White.copy(alpha = 0.18f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "PUBLICACIONES PENDIENTES DE REVISIÓN",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = subTextColor,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

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
                            color = mainTextColor
                        )
                        Text(
                            text = "No hay publicaciones pendientes de moderación.",
                            fontSize = 13.sp,
                            color = subTextColor
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
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
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
                                                color = Color(0xFFFFF8E1),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text(
                                                    text = item.categoria.name,
                                                    color = Color(0xFFF57F17),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                                )
                                            }

                                            Text(
                                                text = item.fechaCreacion,
                                                fontSize = 11.sp,
                                                color = subTextColor
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = item.nombre,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = mainTextColor,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = "por ${item.nombrePublicante}",
                                            fontSize = 12.sp,
                                            color = subTextColor
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // AI Analysis Card
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
                                                text = "Resumen de Moderación IA",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = Color(0xFF7B1FA2)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Publicación en categoría ${item.categoria.name}. Descripción coherente. Raza: ${item.raza}. Vacunas: ${item.vacunasDetalle}.",
                                            fontSize = 12.sp,
                                            color = Color(0xFF4A148C),
                                            lineHeight = 16.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.aprobarPublicacion(item.id, item.nombre)
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Publicación verificada. ¡Aparecerá inmediatamente en el Feed!")
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
                                        Text("Aprobar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
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

    selectedRejectPost?.let { post ->
        AlertDialog(
            onDismissRequest = { selectedRejectPost = null },
            title = { Text("Rechazar publicación", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Escribe el motivo del rechazo para \"${post.nombre}\":",
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
                            viewModel.rechazarPublicacion(post.id, post.nombre, rejectReason)
                            selectedRejectPost = null
                            scope.launch {
                                snackbarHostState.showSnackbar("Publicación rechazada y notificada al usuario")
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
