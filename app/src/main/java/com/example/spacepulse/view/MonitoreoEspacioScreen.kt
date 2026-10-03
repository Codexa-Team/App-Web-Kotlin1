package com.example.spacepulse.view

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.WifiTethering
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitoreoEspacioScreen(navController: NavController, spaceViewModel: SpaceViewModel, spaceId: Long) {
    val darkBlue = Color(0xFF2C3E50)
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
    val token = sharedPref.getString("USER_TOKEN", "") ?: ""
    val userId = sharedPref.getString("USER_ID", "") ?: ""

    val iotDevices by spaceViewModel.iotDevices.collectAsState()
    val spaces by spaceViewModel.spaces.collectAsState()
    val space = spaces.find { it.id == spaceId }

    LaunchedEffect(spaceId) {
        if (token.isNotEmpty()) {
            spaceViewModel.fetchIoTDevices(token, spaceId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Monitoreo", fontWeight = FontWeight.Bold, color = darkBlue, fontSize = 22.sp)
                        Text("Estado del espacio", color = Color.Gray, fontSize = 14.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = darkBlue)
                    }
                },
                actions = {
                    if (iotDevices.isNotEmpty()) {
                        IconButton(onClick = { navController.navigate("agregarIoTDevice/$spaceId") }) {
                            Icon(Icons.Filled.Add, contentDescription = "Agregar", tint = darkBlue)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        if (iotDevices.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.WifiTethering,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color.LightGray
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Monitoreo no disponible", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = darkBlue)
                Spacer(modifier = Modifier.height(12.dp))
                if (space?.hasIot == true) {
                    Text(
                        text = "Este espacio aún no tiene sensores\nactivos",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { navController.navigate("agregarIoTDevice/$spaceId") },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD3A076))
                    ) {
                        Text("Agregar dispositivo", fontSize = 18.sp, color = Color.White)
                    }
                } else {
                    Text(
                        text = "La tecnología IoT está desactivada\npara este espacio",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { spaceViewModel.enableIotForSpace(token, userId, spaceId) },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
                    ) {
                        Text("Activar Tecnología IoT", fontSize = 18.sp, color = Color.White)
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = darkBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = space?.title ?: "Espacio", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Monitoreo limitado", color = Color.LightGray, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Accesorios vinculados", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(iotDevices) { device ->
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
                                    Icon(Icons.Filled.Router, contentDescription = null, tint = Color.Gray)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(text = device.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = darkBlue)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "${device.type} - ${device.serialNumber}", color = Color.Gray, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}