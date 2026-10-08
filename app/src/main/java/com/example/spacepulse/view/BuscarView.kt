package com.example.spacepulse.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.spacepulse.viewmodel.SpaceViewModel

@Composable
fun BuscarView(
    navController: NavController,
    spaceViewModel: SpaceViewModel
) {
    val darkBlue = Color(0xFF064B78)
    val lightBackground = Color(0xFFF8F9FA)
    val borderGray = Color(0xFFE0E0E0)

    var vehicleQuery by remember { mutableStateOf("") }
    var brandFilter by remember { mutableStateOf("") }
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }

    val vehicles by spaceViewModel.vehicles.collectAsState()

    // Filtrar los vehículos del backend en tiempo real
    val filteredVehicles = remember(vehicles, vehicleQuery, brandFilter, minPrice, maxPrice) {
        vehicles.filter { v ->
            val matchesQuery = vehicleQuery.isBlank() ||
                    v.model.contains(vehicleQuery, ignoreCase = true) ||
                    v.brand.contains(vehicleQuery, ignoreCase = true)
            val matchesBrand = brandFilter.isBlank() ||
                    v.brand.contains(brandFilter, ignoreCase = true)
            val minVal = minPrice.toDoubleOrNull()
            val maxVal = maxPrice.toDoubleOrNull()
            val matchesMin = minVal == null || v.pricePerDay >= minVal
            val matchesMax = maxVal == null || v.pricePerDay <= maxVal

            matchesQuery && matchesBrand && matchesMin && matchesMax
        }
    }

    LaunchedEffect(Unit) {
        spaceViewModel.fetchVehicles()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lightBackground)
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Buscar Vehículos",
            color = darkBlue,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Filtra por marca, modelo o rango de precios",
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de búsqueda principal
        OutlinedTextField(
            value = vehicleQuery,
            onValueChange = { vehicleQuery = it },
            placeholder = { Text("Buscar modelo o marca...", color = Color.Gray) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Buscar",
                    tint = darkBlue
                )
            },
            trailingIcon = {
                if (vehicleQuery.isNotEmpty()) {
                    IconButton(onClick = { vehicleQuery = "" }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Limpiar", tint = Color.Gray)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = searchFieldColors(borderGray, darkBlue, Color.White)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Filtro por marca
        Text(
            text = "Marca:",
            color = darkBlue,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = brandFilter,
            onValueChange = { brandFilter = it },
            placeholder = { Text("Ej. Toyota, Hyundai, Nissan...", color = Color.Gray) },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = searchFieldColors(borderGray, darkBlue, Color.White)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Filtro por rango de precio por día
        Text(
            text = "Rango de precio por día ($):",
            color = darkBlue,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = minPrice,
                onValueChange = { minPrice = it },
                placeholder = { Text("Mínimo", color = Color.Gray) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f),
                colors = searchFieldColors(borderGray, darkBlue, Color.White)
            )

            OutlinedTextField(
                value = maxPrice,
                onValueChange = { maxPrice = it },
                placeholder = { Text("Máximo", color = Color.Gray) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f),
                colors = searchFieldColors(borderGray, darkBlue, Color.White)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Resultados
        Text(
            text = "Resultados (${filteredVehicles.size})",
            color = darkBlue,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredVehicles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontraron vehículos con estos filtros",
                    color = Color.Gray,
                    fontSize = 15.sp
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                filteredVehicles.forEach { vehicle ->
                    VehicleCard(
                        vehicle = vehicle,
                        onClick = { navController.navigate("detalleEspacio/${vehicle.id}") }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun searchFieldColors(
    borderGray: Color,
    darkBlue: Color,
    background: Color
) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = darkBlue,
    unfocusedBorderColor = borderGray,
    focusedContainerColor = background,
    unfocusedContainerColor = background
)
