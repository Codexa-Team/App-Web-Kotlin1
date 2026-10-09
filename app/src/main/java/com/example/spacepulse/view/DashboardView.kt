package com.example.spacepulse.view

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.spacepulse.R
import com.example.spacepulse.model.beans.VehicleResource
import com.example.spacepulse.model.client.RetrofitClient
import com.example.spacepulse.viewmodel.AuthViewModel
import com.example.spacepulse.viewmodel.SpaceViewModel

@Composable
fun DashboardView(
    navController: NavController,
    spaceViewModel: SpaceViewModel,
    authViewModel: AuthViewModel,
    onTabSelected: (Int) -> Unit
) {
    val context = LocalContext.current
    val darkBlue = Color(0xFF2C3E50)
    val lightBackground = Color(0xFFF8F9FA)
    val accentBlue = Color(0xFF4DB7ED)

    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
    val fullName = sharedPref.getString("USER_FULL_NAME", "Usuario") ?: "Usuario"
    val firstName = fullName.split(" ").firstOrNull() ?: "Usuario"
    val token = sharedPref.getString("USER_TOKEN", "") ?: ""
    val userId = sharedPref.getString("USER_ID", "") ?: ""
    val userRole = sharedPref.getString("USER_ROLE", "") ?: ""

    val vehicles by spaceViewModel.vehicles.collectAsState()
    val isLoading by spaceViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        if (token.isNotEmpty()) {
            RetrofitClient.authToken = token
            authViewModel.fetchProfile(token, userId)
        }
        spaceViewModel.fetchVehicles()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Encabezado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Hola, $firstName", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = darkBlue)
                Text(text = "Encuentra tu próximo auto", fontSize = 15.sp, color = Color.Gray)
            }
            Image(
                painter = painterResource(id = R.drawable.renticar2),
                contentDescription = "Logo Renticar",
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))


        // Botón para publicar si es Arrendador
        if (userRole.contains("arrendador", ignoreCase = true) || userRole.contains("owner", ignoreCase = true)) {
            Button(
                onClick = { navController.navigate("crearEspacio") },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27AE60)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Publicar nuevo vehículo", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Título de Catálogo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Vehículos Disponibles", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = darkBlue)
            TextButton(onClick = { spaceViewModel.fetchVehicles() }) {
                Text("Actualizar", color = accentBlue, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading && vehicles.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = darkBlue)
            }
        } else if (vehicles.isEmpty()) {
            Card(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Filled.DirectionsCar, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "No hay vehículos publicados aún", color = Color.Gray, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = { navController.navigate("crearEspacio") }) {
                        Text("Sé el primero en publicar uno")
                    }
                }
            }
        } else {
            // Lista de tarjetas de vehículos
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                vehicles.forEach { vehicle ->
                    VehicleCard(
                        vehicle = vehicle,
                        onClick = { navController.navigate("detalleEspacio/${vehicle.id}") }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun VehicleCard(vehicle: VehicleResource, onClick: () -> Unit) {
    val darkBlue = Color(0xFF2C3E50)
    val accentGreen = Color(0xFF27AE60)

    val resolvedImageUrl = RetrofitClient.resolveImageUrl(vehicle.imageUrl)

    Card(
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E7E9)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            // Imagen del auto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color(0xFFF0F4F8))
            ) {
                if (!resolvedImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = resolvedImageUrl,
                        contentDescription = "${vehicle.brand} ${vehicle.model}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.renticar2),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.Center),
                        contentScale = ContentScale.Fit
                    )
                }

                // Badge de estado
                Surface(
                    color = accentGreen.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(topStart = 0.dp, bottomStart = 8.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = vehicle.status ?: "Disponible",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Datos del auto
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${vehicle.brand} ${vehicle.model}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkBlue
                        )
                        Text(
                            text = "Año ${vehicle.year}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "$${vehicle.pricePerDay}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF064B78)
                        )
                        Text(
                            text = "/ día",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = darkBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Ver detalles y reservar", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}