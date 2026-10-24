package co.edu.uniqundio.tupataamiga.features.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NotificacionUiModel(
    val id: String,
    val titulo: String,
    val cuerpo: String,
    val tiempo: String,
    val tipo: TipoNotificacion,
    val leido: Boolean
)

enum class TipoNotificacion {
    VERIFICADA,
    INTERES,
    COMENTARIO,
    LOGRO,
    RECHAZADA
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen() {
    val brandGreen = Color(0xFF0F4C25)

    var notificaciones by remember {
        mutableStateOf(
            listOf(
                NotificacionUiModel(
                    id = "1",
                    titulo = "Publicación verificada",
                    cuerpo = "Tu publicación \"Rex busca hogar\" fue verificada por un moderador.",
                    tiempo = "hace 30 min",
                    tipo = TipoNotificacion.VERIFICADA,
                    leido = false
                ),
                NotificacionUiModel(
                    id = "2",
                    titulo = "5 personas interesadas",
                    cuerpo = "5 personas marcaron interés en adoptar a Rex.",
                    tiempo = "hace 1 h",
                    tipo = TipoNotificacion.INTERES,
                    leido = false
                ),
                NotificacionUiModel(
                    id = "3",
                    titulo = "Nuevo comentario",
                    cuerpo = "Ana López comentó en tu publicación \"Rex busca hogar\".",
                    tiempo = "hace 2 h",
                    tipo = TipoNotificacion.COMENTARIO,
                    leido = false
                ),
                NotificacionUiModel(
                    id = "4",
                    titulo = "¡Nuevo logro!",
                    cuerpo = "Obtuviste la insignia \"Primera Huella\" por tu primera publicación.",
                    tiempo = "hace 1 día",
                    tipo = TipoNotificacion.LOGRO,
                    leido = true
                ),
                NotificacionUiModel(
                    id = "5",
                    titulo = "Publicación rechazada",
                    cuerpo = "Motivo: las imágenes no corresponden a la descripción.",
                    tiempo = "hace 2 días",
                    tipo = TipoNotificacion.RECHAZADA,
                    leido = true
                )
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Notificaciones",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 22.sp
                    )
                },
                actions = {
                    TextButton(
                        onClick = {
                            notificaciones = notificaciones.map { it.copy(leido = true) }
                        }
                    ) {
                        Text(
                            text = "Marcar leídas",
                            color = brandGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF6FBF7)
                )
            )
        },
        containerColor = Color(0xFFF6FBF7)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF6FBF7))
        ) {
            items(notificaciones, key = { it.id }) { notif ->
                val (icon, iconBg, iconFg) = when (notif.tipo) {
                    TipoNotificacion.VERIFICADA -> Triple(
                        Icons.Default.CheckCircle,
                        Color(0xFFE8F5E9),
                        Color(0xFF2E7D32)
                    )
                    TipoNotificacion.INTERES -> Triple(
                        Icons.Default.Favorite,
                        Color(0xFFFFEBEE),
                        Color(0xFFE53935)
                    )
                    TipoNotificacion.COMENTARIO -> Triple(
                        Icons.Default.Comment,
                        Color(0xFFF5F5F5),
                        Color(0xFF757575)
                    )
                    TipoNotificacion.LOGRO -> Triple(
                        Icons.Default.EmojiEvents,
                        Color(0xFFFFF8E1),
                        Color(0xFFF57F17)
                    )
                    TipoNotificacion.RECHAZADA -> Triple(
                        Icons.Default.Close,
                        Color(0xFFFFEBEE),
                        Color(0xFFC62828)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            notificaciones = notificaciones.map {
                                if (it.id == notif.id) it.copy(leido = true) else it
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Icon Circle
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(iconBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconFg,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        // Text Body
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = notif.titulo,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.Black
                                )

                                // Unread Dot
                                if (!notif.leido) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(brandGreen)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = notif.cuerpo,
                                fontSize = 13.sp,
                                color = Color.DarkGray,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = notif.tiempo,
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                }
            }
        }
    }
}
