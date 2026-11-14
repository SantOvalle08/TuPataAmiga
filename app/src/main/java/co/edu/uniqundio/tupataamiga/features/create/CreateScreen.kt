package co.edu.uniqundio.tupataamiga.features.create

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(
    viewModel: CreateViewModel,
    onCreated: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var mostrarOpcionesFoto by remember { mutableStateOf(false) }
    var mostrarDialogoUbicacion by remember { mutableStateOf(false) }

    val brandGreen = Color(0xFF0F4C25)
    val fieldBg = Color(0xFFE8F5E9)

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.onFotosAgregadas(uris)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val file = File(context.cacheDir, "pet_${System.currentTimeMillis()}.jpg")
            FileOutputStream(file).use { out ->
                it.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            viewModel.onFotosAgregadas(listOf(Uri.fromFile(file)))
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color(0xFFF6FBF7),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nueva publicación",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            viewModel.createMascota(
                                onSuccess = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("¡Publicación enviada a revisión!")
                                        delay(900)
                                        onCreated()
                                    }
                                },
                                onError = { mensajeError ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(mensajeError)
                                    }
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
                        shape = RoundedCornerShape(20.dp),
                        enabled = !uiState.isLoading,
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                        } else {
                            Text("Publicar", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF6FBF7)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF6FBF7))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. CATEGORÍA
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "CATEGORÍA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(co.edu.uniqundio.tupataamiga.domain.model.Categoria.entries) { cat ->
                        FilterChip(
                            selected = uiState.categoria == cat,
                            onClick = { viewModel.onCategoriaChanged(cat) },
                            label = { Text(cat.name.replace("_", " ")) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = brandGreen,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }

            // 2. Título
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Título",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )
                TextField(
                    value = uiState.nombre,
                    onValueChange = viewModel::onNombreChanged,
                    placeholder = { Text("Rex busca hogar amoroso") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = fieldBg,
                        unfocusedContainerColor = fieldBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }

            // 3. TIPO DE ANIMAL
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "TIPO DE ANIMAL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(co.edu.uniqundio.tupataamiga.domain.model.TipoAnimal.entries) { tipo ->
                        FilterChip(
                            selected = uiState.tipoAnimal == tipo,
                            onClick = { viewModel.onTipoAnimalChanged(tipo) },
                            label = { Text(tipo.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = brandGreen,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }

            // 4. Raza aproximada
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Raza aproximada",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )
                TextField(
                    value = uiState.raza,
                    onValueChange = viewModel::onRazaChanged,
                    placeholder = { Text("Labrador") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = fieldBg,
                        unfocusedContainerColor = fieldBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }

            // 5. TAMAÑO
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "TAMAÑO",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    co.edu.uniqundio.tupataamiga.domain.model.TamanoMascota.entries.forEach { tam ->
                        FilterChip(
                            selected = uiState.tamano == tam,
                            onClick = { viewModel.onTamanoChanged(tam) },
                            label = { Text(tam.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = brandGreen,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }

            // 6. ¿Tiene vacunas?
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = fieldBg)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "¿Tiene vacunas?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "Vacunas y desparasitación",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = uiState.tieneVacunas,
                        onCheckedChange = viewModel::onVacunasChanged,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = brandGreen
                        )
                    )
                }
            }

            // 7. Descripción
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Descripción",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )
                TextField(
                    value = uiState.descripcion,
                    onValueChange = viewModel::onDescripcionChanged,
                    placeholder = { Text("Rex tiene 2 años, es muy cariñoso y juguetón...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = fieldBg,
                        unfocusedContainerColor = fieldBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }

            // 8. FOTOS (Galería o Cámara con previsualización real)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "FOTOS (${uiState.fotosSeleccionadas.size}/5)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (uiState.fotosSeleccionadas.size < 5) {
                        item {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(1.5.dp, brandGreen, RoundedCornerShape(16.dp))
                                    .background(fieldBg)
                                    .clickable { mostrarOpcionesFoto = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Agregar foto",
                                        tint = brandGreen,
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Text(
                                        text = "Agregar",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = brandGreen
                                    )
                                }
                            }
                        }
                    }

                    items(uiState.fotosSeleccionadas) { uri ->
                        Box(modifier = Modifier.size(76.dp)) {
                            AsyncImage(
                                model = uri,
                                contentDescription = "Foto seleccionada",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.65f))
                                    .clickable { viewModel.onFotoEliminada(uri) },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Quitar foto",
                                    tint = Color.White,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 9. UBICACIÓN (Interactiva al tocar "Cambiar")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "UBICACIÓN",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { mostrarDialogoUbicacion = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = fieldBg)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = brandGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = uiState.ubicacion,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        }
                        Text(
                            text = "Cambiar",
                            color = brandGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Diálogo para elegir Galería o Cámara
    if (mostrarOpcionesFoto) {
        AlertDialog(
            onDismissRequest = { mostrarOpcionesFoto = false },
            title = { Text("Agregar foto", fontWeight = FontWeight.Bold) },
            text = { Text("Elige de dónde deseas obtener la imagen de la mascota:") },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarOpcionesFoto = false
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = brandGreen)
                    Spacer(Modifier.width(6.dp))
                    Text("Galería", color = brandGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarOpcionesFoto = false
                        cameraLauncher.launch()
                    }
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = brandGreen)
                    Spacer(Modifier.width(6.dp))
                    Text("Cámara", color = brandGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Diálogo para cambiar ubicación
    if (mostrarDialogoUbicacion) {
        val zonasSugeridas = listOf(
            "El Bosque, Armenia",
            "Centro, Armenia",
            "Plaza de Bolívar, Armenia",
            "La Castellana, Armenia",
            "Universidad del Quindío, Armenia",
            "Parque de la Vida, Armenia"
        )
        var direccionPersonalizada by remember { mutableStateOf(uiState.ubicacion) }

        AlertDialog(
            onDismissRequest = { mostrarDialogoUbicacion = false },
            title = { Text("Seleccionar ubicación", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Elige un sector rápido o escribe una dirección:",
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                    zonasSugeridas.forEach { zona ->
                        val seleccionada = direccionPersonalizada.equals(zona, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (seleccionada) brandGreen else fieldBg,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { direccionPersonalizada = zona }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = if (seleccionada) Color.White else brandGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = zona,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (seleccionada) Color.White else Color.Black
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = direccionPersonalizada,
                        onValueChange = { direccionPersonalizada = it },
                        label = { Text("Dirección o barrio") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onUbicacionChanged(direccionPersonalizada)
                        mostrarDialogoUbicacion = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = brandGreen)
                ) {
                    Text("Guardar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoUbicacion = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}