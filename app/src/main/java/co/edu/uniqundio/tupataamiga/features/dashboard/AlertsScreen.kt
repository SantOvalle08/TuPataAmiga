package co.edu.uniqundio.tupataamiga.features.dashboard

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
    val fieldBg = Color(0xFFE8F5E9)

    var activeFullScreenType by remember { mutableStateOf<TipoNotificacion?>(null) }

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

    when (activeFullScreenType) {
        TipoNotificacion.VERIFICADA -> {
            VerifiedPostsFullScreen(
                onBack = { activeFullScreenType = null },
                brandGreen = brandGreen,
                fieldBg = fieldBg
            )
        }
        TipoNotificacion.INTERES -> {
            InterestedPeopleFullScreen(
                onBack = { activeFullScreenType = null },
                brandGreen = brandGreen,
                fieldBg = fieldBg
            )
        }
        TipoNotificacion.COMENTARIO -> {
            CommentsThreadFullScreen(
                onBack = { activeFullScreenType = null },
                brandGreen = brandGreen,
                fieldBg = fieldBg
            )
        }
        TipoNotificacion.LOGRO -> {
            AchievementDetailFullScreen(
                onBack = { activeFullScreenType = null },
                brandGreen = brandGreen,
                fieldBg = fieldBg
            )
        }
        TipoNotificacion.RECHAZADA -> {
            RejectionDetailFullScreen(
                onBack = { activeFullScreenType = null },
                brandGreen = brandGreen
            )
        }
        null -> {
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
                                    notificaciones = notificaciones.map {
                                        if (it.id == notif.id) it.copy(leido = true) else it
                                    }
                                    activeFullScreenType = notif.tipo
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
    }
}

// 1. Full Screen: Publicaciones Verificadas
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VerifiedPostsFullScreen(
    onBack: () -> Unit,
    brandGreen: Color,
    fieldBg: Color
) {
    val verifiedList = remember {
        listOf(
            VerifiedPostItem("1", "Rex busca un hogar amoroso", "Perro · Labrador · Grande", "El Bosque, Armenia", "Aprobada hoy"),
            VerifiedPostItem("2", "Michi en adopción responsable", "Gato · Siamés · Pequeño", "Centro, Armenia", "Aprobada ayer"),
            VerifiedPostItem("3", "Lucas busca a su familia", "Perro · Beagle · Mediano", "Granada, Armenia", "Aprobada hace 2 días"),
            VerifiedPostItem("4", "Toby el perro rescatado", "Perro · Mestizo · Mediano", "San José, Armenia", "Aprobada hace 3 días"),
            VerifiedPostItem("5", "Mimi en adopción", "Gato · Persa · Pequeño", "La Castellana, Armenia", "Aprobada hace 4 días")
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
                            color = Color.Black
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF6FBF7))
            )
        },
        containerColor = Color(0xFFF6FBF7)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF6FBF7))
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
                            color = Color.DarkGray,
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
                        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                                    color = Color.Black
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
    fieldBg: Color
) {
    val interestedList = remember {
        listOf(
            InterestedUserItem("1", "Ana López", "analopez@gmail.com", "hace 10 min"),
            InterestedUserItem("2", "Carlos Pérez", "carlosperez@gmail.com", "hace 25 min"),
            InterestedUserItem("3", "María Fernanda", "mariaf@gmail.com", "hace 40 min"),
            InterestedUserItem("4", "Juan Gómez", "juangomez@gmail.com", "hace 1 h"),
            InterestedUserItem("5", "Sofía Ramírez", "sofiar@gmail.com", "hace 2 h")
        )
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF6FBF7))
            )
        },
        containerColor = Color(0xFFF6FBF7)
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
                        Text("5 Personas Interesadas en 'Rex'", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFFC62828))
                        Text("Estas personas han marcado interés directo en adoptar a Rex.", fontSize = 12.sp, color = Color.DarkGray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(interestedList, key = { it.id }) { user ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                                onClick = { },
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
    fieldBg: Color
) {
    var nuevoComentario by remember { mutableStateOf("") }
    val comentarios = remember {
        mutableStateListOf(
            CommentItem("1", "Ana López", "¡Hola! Quisiera saber si Rex aún está disponible para adopción, me interesa mucho.", "hace 2 h"),
            CommentItem("2", "Pedro Morales (Autor)", "¡Hola Ana! Sí, sigue disponible. Nos podemos poner en contacto por mensaje directo.", "hace 1 h"),
            CommentItem("3", "Carlos Herrera", "Hermoso perrito, espero encuentre pronto un hogar amoroso.", "hace 30 min")
        )
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF6FBF7))
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
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
        containerColor = Color(0xFFF6FBF7)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(comentarios, key = { it.id }) { comm ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
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
    fieldBg: Color
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF6FBF7))
            )
        },
        containerColor = Color(0xFFF6FBF7)
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

            Text("Primera Huella", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color.Black)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "¡Felicitaciones! Obtuviste esta insignia por realizar tu primera publicación de mascota en PataAmiga. Has ganado +50 puntos de experiencia en la comunidad.",
                fontSize = 14.sp,
                color = Color.DarkGray,
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
    brandGreen: Color
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF6FBF7))
            )
        },
        containerColor = Color(0xFFF6FBF7)
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
                        "\"Las imágenes adjuntas no corresponden a la descripción del animal. Por favor asegúrate de subir fotografías claras e información precisa.\"",
                        fontSize = 13.sp,
                        color = Color.DarkGray,
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
