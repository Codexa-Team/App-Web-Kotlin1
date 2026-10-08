package com.example.spacepulse.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.spacepulse.R
import com.example.spacepulse.model.client.RetrofitClient
import com.example.spacepulse.viewmodel.SpaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleEspacioScreen(
    navController: NavController,
    spaceViewModel: SpaceViewModel,
    spaceId: Long // Corresponde al vehicleId
) {
    val darkBlue = Color(0xFF2C3E50)
    val accentBlue = Color(0xFF4DB7ED)

    val vehicles by spaceViewModel.vehicles.collectAsState()
    val vehicle = vehicles.find { it.id == spaceId } ?: spaceViewModel.selectedVehicle.collectAsState().value

    val reviews by spaceViewModel.reviews.collectAsState()
    val bookingState by spaceViewModel.createBookingState.collectAsState()

    var showBookingDialog by remember { mutableStateOf(false) }
    var startDateText by remember { mutableStateOf("2026-10-10") }
    var endDateText by remember { mutableStateOf("2026-10-14") }

    var showReviewDialog by remember { mutableStateOf(false) }
    var reviewRating by remember { mutableIntStateOf(5) }
    var reviewComment by remember { mutableStateOf("") }

    var alertMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(spaceId) {
        spaceViewModel.fetchVehicleById(spaceId)
        spaceViewModel.fetchVehicleReviews(spaceId)
    }

    LaunchedEffect(bookingState) {
        if (bookingState?.isSuccess == true) {
            alertMessage = "¡Reserva realizada exitosamente! Puedes revisarla en 'Mis Reservas'."
            showBookingDialog = false
            spaceViewModel.resetStates()
        } else if (bookingState?.isFailure == true) {
            alertMessage = bookingState?.exceptionOrNull()?.message ?: "Error al reservar"
        }
    }

    if (alertMessage != null) {
        AlertDialog(
            onDismissRequest = { alertMessage = null },
            title = { Text("Notificación", fontWeight = FontWeight.Bold, color = darkBlue) },
            text = { Text(alertMessage!!) },
            confirmButton = {
                Button(onClick = { alertMessage = null }, colors = ButtonDefaults.buttonColors(containerColor = darkBlue)) {
                    Text("OK")
                }
            }
        )
    }

    // Modal para Reservar Vehículo
    if (showBookingDialog && vehicle != null) {
        AlertDialog(
            onDismissRequest = { showBookingDialog = false },
            title = { Text("Reservar ${vehicle.brand} ${vehicle.model}", fontWeight = FontWeight.Bold, color = darkBlue) },
            text = {
                Column {
                    Text("Precio: $${vehicle.pricePerDay} por día", fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = startDateText,
                        onValueChange = { startDateText = it },
                        label = { Text("Fecha de inicio (AAAA-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = endDateText,
                        onValueChange = { endDateText = it },
                        label = { Text("Fecha de fin (AAAA-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        spaceViewModel.createBooking(spaceId, startDateText.trim(), endDateText.trim())
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
                ) {
                    Text("Confirmar Reserva")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBookingDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }

    // Modal para Dejar Reseña
    if (showReviewDialog) {
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text("Calificar Vehículo", fontWeight = FontWeight.Bold, color = darkBlue) },
            text = {
                Column {
                    Text("Puntuación: $reviewRating estrellas", fontWeight = FontWeight.Medium)
                    Slider(
                        value = reviewRating.toFloat(),
                        onValueChange = { reviewRating = it.toInt() },
                        valueRange = 1f..5f,
                        steps = 3
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = reviewComment,
                        onValueChange = { reviewComment = it },
                        label = { Text("Escribe tu experiencia...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (reviewComment.isNotBlank()) {
                            spaceViewModel.createReview(spaceId, reviewRating, reviewComment.trim())
                            showReviewDialog = false
                            reviewComment = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
                ) {
                    Text("Enviar Reseña")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(vehicle?.let { "${it.brand} ${it.model}" } ?: "Detalle del Vehículo", fontWeight = FontWeight.Bold, color = darkBlue) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = darkBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        if (vehicle == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = darkBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // Imagen principal
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.White, shape = RoundedCornerShape(16.dp))
                ) {
                    val resolvedUrl = RetrofitClient.resolveImageUrl(vehicle.imageUrl)
                    if (!resolvedUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = resolvedUrl,
                            contentDescription = "${vehicle.brand} ${vehicle.model}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.renticar2),
                            contentDescription = null,
                            modifier = Modifier.size(120.dp).align(Alignment.Center)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Ficha del Vehículo
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFE5E7E9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${vehicle.brand} ${vehicle.model}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = darkBlue
                                )
                                Text(
                                    text = "Año ${vehicle.year}",
                                    fontSize = 15.sp,
                                    color = Color.Gray
                                )
                            }
                            Text(
                                text = "$${vehicle.pricePerDay}/día",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF064B78)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showBookingDialog = true },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = darkBlue),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Filled.Event, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reservar este vehículo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))


                // Sección de Reseñas
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFE5E7E9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Reseñas (${reviews.size})", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = darkBlue)
                            TextButton(onClick = { showReviewDialog = true }) {
                                Text("+ Dejar reseña", color = accentBlue)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (reviews.isEmpty()) {
                            Text("Aún no hay reseñas para este vehículo.", fontSize = 14.sp, color = Color.Gray)
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                reviews.forEach { review ->
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Column {
                                            Row {
                                                repeat(review.rating) {
                                                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFF1C40F), modifier = Modifier.size(16.dp))
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(review.comment, fontSize = 14.sp, color = Color.DarkGray)
                                        }
                                    }
                                    HorizontalDivider(color = Color(0xFFF5F5F5))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}