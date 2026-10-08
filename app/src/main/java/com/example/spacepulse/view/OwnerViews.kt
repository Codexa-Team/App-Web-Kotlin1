package com.example.spacepulse.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.spacepulse.model.beans.BookingResource
import com.example.spacepulse.model.beans.VehicleResource
import com.example.spacepulse.model.client.RetrofitClient
import com.example.spacepulse.viewmodel.SpaceViewModel

// ========================================================
// 1. HOME DE PROPIETARIO: MIS VEHÍCULOS PUBLICADOS
// ========================================================
@Composable
fun MisCarrosOwnerView(
    navController: NavController,
    spaceViewModel: SpaceViewModel,
    onNavigateToPublish: () -> Unit
) {
    val darkBlue = Color(0xFF2C3E50)
    val accentBlue = Color(0xFF4DB7ED)
    val lightGray = Color(0xFFF8F9FA)

    val myVehicles by spaceViewModel.myVehicles.collectAsState()
    val allVehicles by spaceViewModel.vehicles.collectAsState()
    val deleteState by spaceViewModel.deleteSpaceState.collectAsState()

    var editingVehicle by remember { mutableStateOf<VehicleResource?>(null) }
    var vehicleToDelete by remember { mutableStateOf<VehicleResource?>(null) }

    LaunchedEffect(Unit) {
        spaceViewModel.fetchMyListings()
        spaceViewModel.fetchVehicles()
    }

    LaunchedEffect(deleteState) {
        if (deleteState?.isSuccess == true) {
            spaceViewModel.fetchMyListings()
            spaceViewModel.fetchVehicles()
            spaceViewModel.resetStates()
        }
    }

    // Modal de Edición de Vehículo
    if (editingVehicle != null) {
        val v = editingVehicle!!
        var editBrand by remember { mutableStateOf(v.brand) }
        var editModel by remember { mutableStateOf(v.model) }
        var editYear by remember { mutableStateOf(v.year.toString()) }
        var editPrice by remember { mutableStateOf(v.pricePerDay.toString()) }

        AlertDialog(
            onDismissRequest = { editingVehicle = null },
            title = {
                Text("Editar Vehículo", fontWeight = FontWeight.Bold, color = darkBlue)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editBrand,
                        onValueChange = { editBrand = it },
                        label = { Text("Marca") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editModel,
                        onValueChange = { editModel = it },
                        label = { Text("Modelo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editYear,
                        onValueChange = { editYear = it },
                        label = { Text("Año") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPrice,
                        onValueChange = { editPrice = it },
                        label = { Text("Precio por día (USD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val yearInt = editYear.toIntOrNull() ?: v.year
                        val priceDbl = editPrice.toDoubleOrNull() ?: v.pricePerDay
                        spaceViewModel.updateVehicle(v.id, editBrand, editModel, yearInt, priceDbl)
                        editingVehicle = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
                ) {
                    Text("Guardar Cambios")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingVehicle = null }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }

    // Modal de Confirmación para Eliminar
    if (vehicleToDelete != null) {
        AlertDialog(
            onDismissRequest = { vehicleToDelete = null },
            title = { Text("Eliminar Vehículo", fontWeight = FontWeight.Bold, color = Color.Red) },
            text = { Text("¿Estás seguro de que deseas eliminar este vehículo (${vehicleToDelete?.brand} ${vehicleToDelete?.model})?") },
            confirmButton = {
                Button(
                    onClick = {
                        spaceViewModel.deleteVehicle(vehicleToDelete!!.id)
                        vehicleToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE74C3C))
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { vehicleToDelete = null }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToPublish,
                containerColor = darkBlue,
                contentColor = Color.White,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Publicar Auto", fontWeight = FontWeight.Bold) }
            )
        },
        containerColor = lightGray
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Mis Vehículos",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = darkBlue
                    )
                    Text(
                        text = "Administra los autos que tienes en arriendo",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
                IconButton(onClick = {
                    spaceViewModel.fetchMyListings()
                    spaceViewModel.fetchVehicles()
                }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Actualizar", tint = darkBlue)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Usar myVehicles o filtrar allVehicles si myVehicles está vacío
            val displayList = if (myVehicles.isNotEmpty()) myVehicles else allVehicles

            if (displayList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.DirectionsCar,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Aún no has publicado ningún vehículo",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToPublish,
                            colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
                        ) {
                            Text("Publicar mi primer auto")
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(displayList) { vehicle ->
                        OwnerVehicleCard(
                            vehicle = vehicle,
                            onEdit = { editingVehicle = vehicle },
                            onDelete = { vehicleToDelete = vehicle }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OwnerVehicleCard(
    vehicle: VehicleResource,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val darkBlue = Color(0xFF2C3E50)
    val accentBlue = Color(0xFF4DB7ED)
    val isAvailable = (vehicle.status ?: "available").lowercase() == "available"

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7E9)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Imagen del vehículo
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFEAECEE))
                ) {
                    AsyncImage(
                        model = RetrofitClient.resolveImageUrl(vehicle.imageUrl) ?: vehicle.imageUrl,
                        contentDescription = vehicle.model,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${vehicle.brand} ${vehicle.model}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = darkBlue
                    )
                    Text(
                        text = "Año: ${vehicle.year}",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$${vehicle.pricePerDay} / día",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = accentBlue
                    )
                }

                // Badge de Estado
                Surface(
                    color = if (isAvailable) Color(0xFFE8F8F5) else Color(0xFFFEF9E7),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(
                        1.dp,
                        if (isAvailable) Color(0xFF2ECC71).copy(alpha = 0.5f) else Color(0xFFF39C12).copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (isAvailable) Color(0xFF2ECC71) else Color(0xFFF39C12))
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isAvailable) "Disponible" else "Rentado",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isAvailable) Color(0xFF27AE60) else Color(0xFFD68910)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF2F4F4))
            Spacer(modifier = Modifier.height(8.dp))

            // Botones de Acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Editar", fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                OutlinedButton(
                    onClick = onDelete,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE74C3C)),
                    border = BorderStroke(1.dp, Color(0xFFE74C3C).copy(alpha = 0.5f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Eliminar", fontSize = 13.sp)
                }
            }
        }
    }
}

// ========================================================
// 2. SOLICITUDES RECIBIDAS (ARRENDADOR)
// ========================================================
@Composable
fun SolicitudesOwnerView(
    navController: NavController,
    spaceViewModel: SpaceViewModel
) {
    val darkBlue = Color(0xFF2C3E50)
    val lightGray = Color(0xFFF8F9FA)

    val requests by spaceViewModel.myRequests.collectAsState()
    val vehicles by spaceViewModel.vehicles.collectAsState()
    val actionState by spaceViewModel.bookingActionState.collectAsState()

    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        spaceViewModel.fetchMyRequests()
        spaceViewModel.fetchVehicles()
    }

    LaunchedEffect(actionState) {
        if (actionState?.isSuccess == true) {
            feedbackMessage = actionState?.getOrNull() ?: "Operación realizada"
            spaceViewModel.fetchMyRequests()
            spaceViewModel.resetStates()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightGray)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Solicitudes Recibidas",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkBlue
                )
                Text(
                    text = "Acepta o declina las reservas de clientes",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
            IconButton(onClick = { spaceViewModel.fetchMyRequests() }) {
                Icon(Icons.Filled.Refresh, contentDescription = "Refrescar", tint = darkBlue)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (feedbackMessage != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD4EDDA)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF155724))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(feedbackMessage!!, color = Color(0xFF155724), fontSize = 14.sp)
                }
            }
        }

        if (requests.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Inbox,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No tienes solicitudes pendientes",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(requests) { request ->
                    val vehicle = vehicles.find { it.id == request.vehicleId }
                    OwnerRequestCard(
                        booking = request,
                        vehicleName = if (vehicle != null) "${vehicle.brand} ${vehicle.model} (${vehicle.year})" else "Vehículo #${request.vehicleId}",
                        onConfirm = { spaceViewModel.confirmBooking(request.id) },
                        onReject = { spaceViewModel.rejectBooking(request.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun OwnerRequestCard(
    booking: BookingResource,
    vehicleName: String,
    onConfirm: () -> Unit,
    onReject: () -> Unit
) {
    val darkBlue = Color(0xFF2C3E50)
    val status = booking.status.uppercase()
    val isPending = status == "PENDING"

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7E9)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = vehicleName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = darkBlue
                )

                Surface(
                    color = when (status) {
                        "CONFIRMED" -> Color(0xFFD4EDDA)
                        "REJECTED" -> Color(0xFFF8D7DA)
                        "CANCELLED" -> Color(0xFFE2E3E5)
                        else -> Color(0xFFFFF3CD)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = when (status) {
                            "CONFIRMED" -> "Aceptada"
                            "REJECTED" -> "Declinada"
                            "CANCELLED" -> "Cancelada"
                            else -> "Pendiente"
                        },
                        color = when (status) {
                            "CONFIRMED" -> Color(0xFF155724)
                            "REJECTED" -> Color(0xFF721C24)
                            "CANCELLED" -> Color(0xFF383D41)
                            else -> Color(0xFF856404)
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.DateRange, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${booking.startDate}  ⭢  ${booking.endDate}",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.AttachMoney, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Total estimado: $${booking.totalPrice}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF27AE60)
                )
            }

            if (isPending) {
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFFF2F4F4))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE74C3C)),
                        border = BorderStroke(1.dp, Color(0xFFE74C3C).copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Declinar")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = onConfirm,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27AE60))
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Aceptar")
                    }
                }
            }
        }
    }
}
