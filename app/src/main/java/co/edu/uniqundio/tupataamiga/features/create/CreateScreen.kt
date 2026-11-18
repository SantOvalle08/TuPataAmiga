package co.edu.uniqundio.tupataamiga.features.create

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.net.Uri
import android.view.MotionEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Place
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
import androidx.core.content.ContextCompat
import co.edu.uniqundio.tupataamiga.domain.model.Categoria
import co.edu.uniqundio.tupataamiga.domain.model.TamanoMascota
import co.edu.uniqundio.tupataamiga.domain.model.TipoAnimal
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(
    viewModel: CreateViewModel,
    onCreated: () -> Unit,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val brandGreen = Color(0xFF0F4C25)
    val fieldBg = Color(0xFFE8F5E9)

    var selectedImageUri by remember { mutableStateOf<Uri?>(value = null) }
    var mostrarSelectorUbicacion by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            viewModel.onFotoPrincipalSeleccionada(uri)
            scope.launch {
                snackbarHostState.showSnackbar("Imagen seleccionada de la galería")
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            scope.launch {
                snackbarHostState.showSnackbar("Permiso de cámara concedido. Abriendo selector...")
            }
            galleryLauncher.launch("image/*")
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Permiso de cámara denegado")
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.onUbicacionChanged("Armenia, Quindío (GPS)")
            scope.launch {
                snackbarHostState.showSnackbar("Ubicación GPS obtenida exitosamente")
            }
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Permiso de ubicación denegado")
            }
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
                        fontSize = 20.sp,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = Color.Black,
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            viewModel.createMascota(
                                onSuccess = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("¡Publicación creada exitosamente!")
                                        delay(1000.milliseconds)
                                        onCreated()
                                    }
                                },
                                onError = { mensajeError ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(mensajeError)
                                    }
                                },
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
                        shape = RoundedCornerShape(20.dp),
                        enabled = !uiState.isLoading,
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp),
                        modifier = Modifier.padding(end = 12.dp),
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                        } else {
                            Text("Publicar", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF6FBF7),
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF6FBF7))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            // 1. CATEGORÍA (Pills)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "CATEGORÍA",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp,
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(Categoria.entries) { cat ->
                        FilterChip(
                            selected = uiState.categoria == cat,
                            onClick = { viewModel.onCategoriaChanged(cat) },
                            label = { Text(cat.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = brandGreen,
                                selectedLabelColor = Color.White,
                            ),
                            shape = RoundedCornerShape(16.dp),
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
                    color = Color.DarkGray,
                )
                val titleHint = when (uiState.categoria) {
                    Categoria.ADOPCION -> "Ej. Luna busca una familia"
                    Categoria.PERDIDO -> "Ej. Max se perdió en el centro"
                    Categoria.ENCONTRADO -> "Ej. Encontré un perro en el parque"
                    Categoria.HOGAR_TEMPORAL -> "Ej. Se necesita hogar temporal"
                    Categoria.JORNADA_VETERINARIA -> "Ej. Jornada de vacunación gratuita"
                }
                TextField(
                    value = uiState.nombre,
                    onValueChange = viewModel::onNombreChanged,
                    placeholder = { Text(titleHint) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = fieldBg,
                        unfocusedContainerColor = fieldBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    singleLine = true,
                )
            }

            // 3. TIPO DE ANIMAL (Chips)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "TIPO DE ANIMAL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp,
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(TipoAnimal.entries) { tipo ->
                        FilterChip(
                            selected = uiState.tipoAnimal == tipo,
                            onClick = { viewModel.onTipoAnimalChanged(tipo) },
                            label = { Text(tipo.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = brandGreen,
                                selectedLabelColor = Color.White,
                            ),
                            shape = RoundedCornerShape(16.dp),
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
                    color = Color.DarkGray,
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
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    singleLine = true,
                )
            }

            // 5. TAMAÑO (Chips)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "TAMAÑO",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Pequeño", "Mediano", "Grande").forEach { tam ->
                        FilterChip(
                            selected = when (tam) {
                                "Pequeño" -> uiState.tamano == TamanoMascota.PEQUENO
                                "Grande" -> uiState.tamano == TamanoMascota.GRANDE
                                else -> uiState.tamano == TamanoMascota.MEDIANO
                            },
                            onClick = {
                                val tamEnum = when (tam) {
                                    "Pequeño" -> TamanoMascota.PEQUENO
                                    "Grande" -> TamanoMascota.GRANDE
                                    else -> TamanoMascota.MEDIANO
                                }
                                viewModel.onTamanoChanged(tamEnum)
                            },
                            label = { Text(tam) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = brandGreen,
                                selectedLabelColor = Color.White,
                            ),
                            shape = RoundedCornerShape(16.dp),
                        )
                    }
                }
            }

            // 6. ¿Tiene vacunas? Switch Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = fieldBg),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = "¿Tiene vacunas?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.Black,
                        )
                        Text(
                            text = "Vacunas y desparasitación",
                            fontSize = 12.sp,
                            color = Color.Gray,
                        )
                    }
                    Switch(
                        checked = uiState.tieneVacunas,
                        onCheckedChange = viewModel::onVacunasChanged,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = brandGreen,
                        ),
                    )
                }
            }

            // 7. Descripción con Generación por IA
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Descripción",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray,
                    )
                    TextButton(
                        onClick = {
                            viewModel.generarDescripcionIa {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Descripción sugerida con IA ✨")
                                }
                            }
                        },
                        enabled = !uiState.isGeneratingIa,
                        contentPadding = PaddingValues(0.dp),
                    ) {
                        if (uiState.isGeneratingIa) {
                            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = brandGreen)
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = "Generar con IA ✨",
                            color = brandGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
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
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                )
            }

            // 8. FOTOS
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "FOTOS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Gallery Picker (GetContent - No permission needed)
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.5.dp, brandGreen, RoundedCornerShape(16.dp))
                            .background(fieldBg)
                            .clickable {
                                galleryLauncher.launch("image/*")
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Galería",
                            tint = brandGreen,
                            modifier = Modifier.size(28.dp),
                        )
                    }

                    // Camera Launcher (RequestPermission for CAMERA)
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.5.dp, brandGreen, RoundedCornerShape(16.dp))
                            .background(fieldBg)
                            .clickable {
                                val hasCamPerm = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA,
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasCamPerm) {
                                    galleryLauncher.launch("image/*")
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Cámara",
                            tint = brandGreen,
                            modifier = Modifier.size(28.dp),
                        )
                    }

                    // Preview of selected photo if available
                    if (selectedImageUri != null) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(fieldBg),
                            contentAlignment = Alignment.Center,
                        ) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Foto elegida",
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                }
            }

            // 9. UBICACIÓN
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "UBICACIÓN",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp,
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = fieldBg),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = brandGreen,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = uiState.ubicacion,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Elegir en mapa",
                                color = brandGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.clickable { mostrarSelectorUbicacion = true },
                            )
                            Text(
                                text = "Obtener GPS",
                                color = brandGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.clickable {
                                    val hasLocPerm = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (hasLocPerm) {
                                        viewModel.onUbicacionChanged("Armenia, Quindío (GPS)")
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Ubicación GPS obtenida exitosamente")
                                        }
                                    } else {
                                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                    }
                                },
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (mostrarSelectorUbicacion) {
        LocationPickerDialog(
            initialAddress = uiState.ubicacion,
            onDismiss = { mostrarSelectorUbicacion = false },
            onLocationSelected = { address, latitude, longitude ->
                viewModel.onUbicacionSeleccionada(address, latitude, longitude)
                mostrarSelectorUbicacion = false
            },
        )
    }
}

@Composable
private fun LocationPickerDialog(
    initialAddress: String,
    onDismiss: () -> Unit,
    onLocationSelected: (String, Double, Double) -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var address by remember { mutableStateOf(initialAddress) }
    var point by remember { mutableStateOf(GeoPoint(4.5375, -75.6780)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecciona la ubicación de la publicación") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(address, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    factory = { ctx ->
                        Configuration.getInstance().load(
                            ctx,
                            android.preference.PreferenceManager.getDefaultSharedPreferences(ctx),
                        )
                        MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            controller.setZoom(14.5)
                            controller.setCenter(point)
                            val marker = Marker(this).apply {
                                position = point
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            }
                            overlays.add(marker)
                            setOnTouchListener { view, event ->
                                if (event.action == MotionEvent.ACTION_UP) {
                                    val selectedPoint = (view as MapView).projection
                                        .fromPixels(event.x.toInt(), event.y.toInt())
                                    point = GeoPoint(selectedPoint.latitude, selectedPoint.longitude)
                                    marker.position = point
                                    (view as MapView).invalidate()
                                    scope.launch {
                                        val resolvedAddress = withContext(Dispatchers.IO) {
                                            runCatching {
                                                Geocoder(context).getFromLocation(
                                                    selectedPoint.latitude,
                                                    selectedPoint.longitude,
                                                    1,
                                                )?.firstOrNull()?.getAddressLine(0)
                                            }.getOrNull()
                                        }
                                        address = resolvedAddress
                                            ?: "${selectedPoint.latitude}, ${selectedPoint.longitude}"
                                    }
                                }
                                false
                            }
                        }
                    },
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onLocationSelected(address, point.latitude, point.longitude)
            }) {
                Text("Usar ubicación")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
    )
}
