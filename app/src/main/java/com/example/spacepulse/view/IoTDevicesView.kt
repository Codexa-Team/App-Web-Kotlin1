package com.example.spacepulse.view

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.SensorsOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.spacepulse.viewmodel.SpaceViewModel

@Composable
fun IoTDevicesView(navController: NavController, spaceViewModel: SpaceViewModel) {
    val darkBlue = Color(0xFF2C3E50)
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
    val token = sharedPref.getString("USER_TOKEN", "") ?: ""
    val userId = sharedPref.getString("USER_ID", "") ?: ""

    val myDevices by spaceViewModel.myIoTDevices.collectAsState()
    val iotDevices by spaceViewModel.iotDevices.collectAsState()
    val spaces by spaceViewModel.spaces.collectAsState()
    
    var selectedSpaceId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(token, userId) {
        if (token.isNotEmpty()) {
            spaceViewModel.fetchSpaces(token, userId)
            spaceViewModel.fetchMyIoTDevices(token)
        }
    }

    LaunchedEffect(selectedSpaceId) {
        if (token.isNotEmpty()) {
            selectedSpaceId?.let {
                spaceViewModel.fetchIoTDevices(token, it)
            }
        }
    }

    val displayDevices = if (selectedSpaceId == null) myDevices else iotDevices

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(text = "Tus Dispositivos IoT", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = darkBlue)
        Text(text = "Administra tus sensores y conexiones", fontSize = 16.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Seleccionar Espacio", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = darkBlue)
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                SpaceChip(
                    text = "Todos",
                    selected = selectedSpaceId == null,
                    onClick = { selectedSpaceId = null }
                )
            }
            items(spaces) { space ->
                SpaceChip(
                    text = space.title,
                    selected = selectedSpaceId == space.id,
                    onClick = { selectedSpaceId = space.id }
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        if (displayDevices.isEmpty()) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(imageVector = Icons.Filled.SensorsOff, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "No hay dispositivos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = darkBlue)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (selectedSpaceId == null) "Agrega dispositivos a tus espacios\npara verlos aquí" 
                           else "No se encontraron dispositivos en este espacio", 
                    fontSize = 16.sp, 
                    color = Color.Gray, 
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(displayDevices) { device ->
                    val spaceName = spaces.find { it.id == device.spaceId }?.title ?: "Espacio desconocido"
                    
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .clickable { navController.navigate("detalleIoTDevice/${device.id}") }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                                Box(modifier = Modifier.size(48.dp).background(Color(0xFFF5F6F8), shape = CircleShape), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Filled.Router, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = device.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = darkBlue)
                                    Text(text = "En: $spaceName", color = darkBlue.copy(alpha = 0.7f), fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = device.type, color = Color.Gray, fontSize = 13.sp)
                                }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpaceChip(text: String, selected: Boolean, onClick: () -> Unit) {
    val darkBlue = Color(0xFF2C3E50)
    val borderGray = Color(0xFFE0E0E0)

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) darkBlue else Color.White
        ),
        border = if (selected) null else BorderStroke(1.dp, borderGray),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (selected) Color.White else Color.Gray
        )
    }
}
