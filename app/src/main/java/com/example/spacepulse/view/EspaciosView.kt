package com.example.spacepulse.view

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.spacepulse.model.beans.BookingResource
import com.example.spacepulse.viewmodel.SpaceViewModel

@Composable
fun EspaciosView(navController: NavController, spaceViewModel: SpaceViewModel) {
    val darkBlue = Color(0xFF2C3E50)
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
    val userRole = sharedPref.getString("USER_ROLE", "") ?: ""

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Mis Reservas, 1: Solicitudes recibidas

    val myBookings by spaceViewModel.myBookings.collectAsState()
    val myRequests by spaceViewModel.myRequests.collectAsState()
    val actionState by spaceViewModel.bookingActionState.collectAsState()

    LaunchedEffect(Unit) {
        spaceViewModel.fetchMyBookings()
        spaceViewModel.fetchMyRequests()
    }

    LaunchedEffect(actionState) {
        if (actionState?.isSuccess == true) {
            spaceViewModel.fetchMyBookings()
            spaceViewModel.fetchMyRequests()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Text(text = "Gestión de Reservas", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = darkBlue)
        Text(text = "Revisa el estado de tus alquileres", fontSize = 15.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        // Tabs si es arrendador o para ver ambas
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFFF0F4F8),
            contentColor = darkBlue
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Mis Reservas", fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Solicitudes Recibidas", fontWeight = FontWeight.SemiBold) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val currentList = if (selectedTab == 0) myBookings else myRequests

        if (currentList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 50.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.EventNote,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (selectedTab == 0) "No has realizado ninguna reserva aún" else "No tienes solicitudes de alquiler pendientes",
                        color = Color.Gray,
                        fontSize = 15.sp
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(currentList) { booking ->
                    BookingCard(
                        booking = booking,
                        isOwnerView = selectedTab == 1,
                        onConfirm = { spaceViewModel.confirmBooking(booking.id) },
                        onReject = { spaceViewModel.rejectBooking(booking.id) },
                        onCancel = { spaceViewModel.cancelBooking(booking.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun BookingCard(
    booking: BookingResource,
    isOwnerView: Boolean,
    onConfirm: () -> Unit,
    onReject: () -> Unit,
    onCancel: () -> Unit
) {
    val darkBlue = Color(0xFF2C3E50)

    val (statusLabel, statusColor) = when (booking.status.uppercase()) {
        "CONFIRMED" -> Pair("Confirmada", Color(0xFF27AE60))
        "PENDING" -> Pair("Pendiente", Color(0xFFF39C12))
        "CANCELLED" -> Pair("Cancelada", Color(0xFF95A5A6))
        "REJECTED" -> Pair("Rechazada", Color(0xFFE74C3C))
        else -> Pair(booking.status, Color.Gray)
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reserva #${booking.id}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkBlue
                )
                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = statusLabel,
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Vehículo ID: ${booking.vehicleId}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Desde: ${booking.startDate} - Hasta: ${booking.endDate}",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Total: $${booking.totalPrice}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF064B78)
            )

            // Botones de acción según el rol y estado
            if (isOwnerView && booking.status.equals("PENDING", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f).height(40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27AE60)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Confirmar", fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f).height(40.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE74C3C)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Filled.Cancel, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rechazar", fontSize = 13.sp)
                    }
                }
            } else if (!isOwnerView && (booking.status.equals("PENDING", ignoreCase = true) || booking.status.equals("CONFIRMED", ignoreCase = true))) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE74C3C)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cancelar Reserva", fontSize = 13.sp)
                }
            }
        }
    }
}