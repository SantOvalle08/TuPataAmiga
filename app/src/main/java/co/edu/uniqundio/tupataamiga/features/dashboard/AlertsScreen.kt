package co.edu.uniqundio.tupataamiga.features.dashboard

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

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
fun AlertsScreen(
    viewModel: AlertsViewModel = hiltViewModel()
) {
    val backgroundColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.backgroundColor
    val cardBg = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.cardColor
    val mainTextColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.textColor
    val subTextColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.subtextColor
    val brandGreen = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.brandGreenColor
    val fieldBg = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.fieldColor

    var activeFullScreenType by remember { mutableStateOf<TipoNotificacion?>(null) }
    var activeNotification by remember { mutableStateOf<NotificacionUiModel?>(null) }
    val notificacionesState by viewModel.notificaciones.collectAsState()
    val publicacionesAprobadas by viewModel.publicacionesAprobadas.collectAsState()
    val usuariosInteresados by viewModel.usuariosInteresados.collectAsState()

    val notificaciones = remember(notificacionesState) {
        notificacionesState.map { domainNotif ->
            val tipoEnum = when (domainNotif.tipo.uppercase()) {
                "VERIFICADA" -> TipoNotificacion.VERIFICADA
                "INTERES" -> TipoNotificacion.INTERES
                "COMENTARIO" -> TipoNotificacion.COMENTARIO
                "LOGRO" -> TipoNotificacion.LOGRO
                else -> TipoNotificacion.RECHAZADA
            }
            NotificacionUiModel(
                id = domainNotif.id,
                titulo = domainNotif.titulo,
                cuerpo = domainNotif.cuerpo,
                tiempo = domainNotif.tiempo,
                tipo = tipoEnum,
                leido = domainNotif.leido
            )
        }
    }

    when (activeFullScreenType) {
        TipoNotificacion.VERIFICADA -> {
            VerifiedPostsFullScreen(
                onBack = { activeFullScreenType = null },
                brandGreen = brandGreen,
                fieldBg = fieldBg,
                mascotas = publicacionesAprobadas,
                notification = activeNotification,
            )
        }
        TipoNotificacion.INTERES -> {
            InterestedPeopleFullScreen(
                onBack = { activeFullScreenType = null },
                brandGreen = brandGreen,
                fieldBg = fieldBg,
                mascotas = publicacionesAprobadas,
                notification = activeNotification,
                usuarios = usuariosInteresados,
            )
        }
        TipoNotificacion.COMENTARIO -> {
            CommentsThreadFullScreen(
                onBack = { activeFullScreenType = null },
                brandGreen = brandGreen,
                fieldBg = fieldBg,
                mascotas = publicacionesAprobadas,
                notification = activeNotification,
            )
        }
        TipoNotificacion.LOGRO -> {
            AchievementDetailFullScreen(
                onBack = { activeFullScreenType = null },
                brandGreen = brandGreen,
                fieldBg = fieldBg,
                notification = activeNotification,
            )
        }
        TipoNotificacion.RECHAZADA -> {
            RejectionDetailFullScreen(
                onBack = { activeFullScreenType = null },
                brandGreen = brandGreen,
                notification = activeNotification,
            )
        }
        null -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Notificaciones", "Notifications"),
                                fontWeight = FontWeight.Bold,
                                color = mainTextColor,
                                fontSize = 22.sp
                            )
                        },
                        actions = {
                            TextButton(
                                onClick = {
                                    viewModel.marcarTodasLeidas()
                                }
                            ) {
                                Text(
                                    text = co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Marcar leídas", "Mark as read"),
                                    color = brandGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = backgroundColor
                        )
                    )
                },
                containerColor = backgroundColor
            ) { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(backgroundColor)
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
                                Icons.AutoMirrored.Filled.Comment,
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
                                    activeFullScreenType = notif.tipo
                                    activeNotification = notif
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
                                        val tituloTraducido = when (notif.tipo) {
                                            TipoNotificacion.VERIFICADA -> co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Publicación verificada", "Verified Post")
                                            TipoNotificacion.INTERES -> co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Personas interesadas", "Interested Adopters")
                                            TipoNotificacion.COMENTARIO -> co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Nuevo comentario", "New Comment")
                                            TipoNotificacion.LOGRO -> co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("¡Nuevo logro!", "New Achievement!")
                                            TipoNotificacion.RECHAZADA -> co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Publicación rechazada", "Post Rejected")
                                        }

                                        Text(
                                            text = tituloTraducido,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = mainTextColor
                                        )

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
                                        color = subTextColor,
                                        lineHeight = 18.sp
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = notif.tiempo,
                                        fontSize = 11.sp,
                                        color = subTextColor
                                    )
                                }
                            }

                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                        }
                    }
                }
            }
        }
    }
}

// 1. Full Screen: Publicaciones Verificadas
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VerifiedPostsFullScreen(
    onBack: () -> Unit,
    brandGreen: Color,
    fieldBg: Color,
    mascotas: List<co.edu.uniqundio.tupataamiga.domain.model.Mascota>,
    notification: NotificacionUiModel?,
) {
    val verifiedList = mascotas.map { mascota ->
        VerifiedPostItem(
            id = mascota.id,
            nombre = mascota.nombre,
            categoria = "${mascota.tipoAnimal.name} · ${mascota.raza} · ${mascota.tamano.name}",
            ubicacion = mascota.ubicacion,
            aprobadaTiempo = mascota.fechaCreacion,
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Publicaciones Verificadas",
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            color = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.textColor
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.backgroundColor)
            )
        },
        containerColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.backgroundColor)
                .padding(horizontal = 16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = fieldBg)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${verifiedList.size} Publicaciones Aprobadas",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            text = "Estas publicaciones han sido revisadas y verificadas por el equipo de moderadores de PataAmiga.",
                            fontSize = 12.sp,
                            color = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.subtextColor,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(verifiedList, key = { it.id }) { post ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.cardColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(fieldBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Pets,
                                    contentDescription = null,
                                    tint = brandGreen,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        color = Color(0xFFE8F5E9),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color(0xFF2E7D32),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Verificada",
                                                color = Color(0xFF2E7D32),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Text(
                                        text = post.aprobadaTiempo,
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = post.nombre,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.textColor
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(text = post.categoria, fontSize = 12.sp, color = Color.Gray)

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = brandGreen,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = post.ubicacion,
                                        fontSize = 12.sp,
                                        color = brandGreen,
                                        fontWeight = FontWeight.Medium
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

// 2. Full Screen: Personas Interesadas
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InterestedPeopleFullScreen(
    onBack: () -> Unit,
    brandGreen: Color,
    fieldBg: Color,
    mascotas: List<co.edu.uniqundio.tupataamiga.domain.model.Mascota>,
    notification: NotificacionUiModel?,
    usuarios: Map<String, co.edu.uniqundio.tupataamiga.domain.model.Usuario>,
) {
    val context = LocalContext.current
    val interestedList = remember(mascotas) {
        mascotas.flatMap { mascota ->
            mascota.interesadosIds.map { userId ->
                InterestedUserItem(
                    id = "${mascota.id}_$userId",
                    nombre = usuarios[userId]?.nombre ?: "Usuario no disponible",
                    correo = usuarios[userId]?.correo ?: "ID: $userId",
                    tiempo = mascota.nombre,
                )
            }
        }.distinctBy { it.correo }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Personas Interesadas", fontWeight = FontWeight.Bold, fontSize = 19.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.backgroundColor)
            )
        },
        containerColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            notification?.titulo ?: "${interestedList.size} Personas Interesadas",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFFC62828),
                        )
                        Text(
                            notification?.cuerpo ?: "Personas que han marcado interés en publicaciones aprobadas.",
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (interestedList.isEmpty()) {
                    item { Text("Todavía no hay intereses registrados.", color = Color.Gray) }
                }
                items(interestedList, key = { it.id }) { user ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.cardColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(brandGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(user.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(user.correo, fontSize = 12.sp, color = Color.Gray)
                                    Text(user.tiempo, fontSize = 11.sp, color = brandGreen)
                                }
                            }

                            Button(
                                onClick = {
                                    if (user.correo.contains("@")) {
                                        val intent = Intent(
                                            Intent.ACTION_SENDTO,
                                            Uri.parse("mailto:${user.correo}"),
                                        ).apply {
                                            putExtra(
                                                Intent.EXTRA_SUBJECT,
                                                "Interés en ${user.tiempo}",
                                            )
                                        }
                                        context.startActivity(intent)
                                    }
                                },
                                enabled = user.correo.contains("@"),
                                colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
                                shape = RoundedCornerShape(16.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Contactar", fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 3. Full Screen: Comentarios
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommentsThreadFullScreen(
    onBack: () -> Unit,
    brandGreen: Color,
    fieldBg: Color,
    mascotas: List<co.edu.uniqundio.tupataamiga.domain.model.Mascota>,
    notification: NotificacionUiModel?,
) {
    var nuevoComentario by remember { mutableStateOf("") }
    val comentarios = remember(mascotas) {
        mutableStateListOf<CommentItem>().apply {
            mascotas.flatMap { it.comentarios }.forEach { comentario ->
                add(
                    CommentItem(
                        id = comentario.id,
                        usuario = comentario.nombreUsuario,
                        texto = comentario.texto,
                        tiempo = comentario.fecha,
                    ),
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comentarios en 'Rex busca hogar'", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.backgroundColor)
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.cardColor)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = nuevoComentario,
                    onValueChange = { nuevoComentario = it },
                    placeholder = { Text("Escribe un comentario...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (nuevoComentario.isNotBlank()) {
                            comentarios.add(
                                CommentItem(
                                    id = System.currentTimeMillis().toString(),
                                    usuario = "Tú",
                                    texto = nuevoComentario,
                                    tiempo = "hace un momento"
                                )
                            )
                            nuevoComentario = ""
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(brandGreen)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Enviar", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        },
        containerColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.backgroundColor
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (comentarios.isEmpty()) {
                item { Text("Todavía no hay comentarios registrados.", color = Color.Gray) }
            }
            if (notification != null) {
                item {
                    Text(notification.cuerpo, color = Color.DarkGray, fontSize = 13.sp)
                }
            }
            items(comentarios, key = { it.id }) { comm ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.cardColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(comm.usuario, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = brandGreen)
                            Text(comm.tiempo, fontSize = 11.sp, color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(comm.texto, fontSize = 13.sp, color = Color.DarkGray, lineHeight = 18.sp)
                    }
                }
            }
        }
    }
}

// 4. Full Screen: Logro Obtenido
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AchievementDetailFullScreen(
    onBack: () -> Unit,
    brandGreen: Color,
    fieldBg: Color,
    notification: NotificacionUiModel?,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Logro e Insignia", fontWeight = FontWeight.Bold, fontSize = 19.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.backgroundColor)
            )
        },
        containerColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF8E1)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFF57F17), modifier = Modifier.size(64.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Surface(color = Color(0xFF4CAF50), shape = RoundedCornerShape(12.dp)) {
                Text("✓ Insignia Desbloqueada", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                notification?.titulo ?: "Logro desbloqueado",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.textColor,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = notification?.cuerpo
                    ?: "Has desbloqueado un nuevo logro por tu actividad en la comunidad.",
                fontSize = 14.sp,
                color = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.subtextColor,
                lineHeight = 22.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Aceptar y Volver", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 5. Full Screen: Rechazo Detalle
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RejectionDetailFullScreen(
    onBack: () -> Unit,
    brandGreen: Color,
    notification: NotificacionUiModel?,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Rechazo", fontWeight = FontWeight.Bold, fontSize = 19.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.backgroundColor)
            )
        },
        containerColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFEBEE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(52.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Publicación Rechazada", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFFC62828))

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Motivo expresado por el moderador:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFC62828))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        notification?.cuerpo
                            ?: "El moderador no aprobó esta publicación. Revisa los datos e inténtalo nuevamente.",
                        fontSize = 13.sp,
                        color = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.subtextColor,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Entendido / Corregir Publicación", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private data class VerifiedPostItem(
    val id: String,
    val nombre: String,
    val categoria: String,
    val ubicacion: String,
    val aprobadaTiempo: String
)

private data class InterestedUserItem(
    val id: String,
    val nombre: String,
    val correo: String,
    val tiempo: String
)

private data class CommentItem(
    val id: String,
    val usuario: String,
    val texto: String,
    val tiempo: String
)
