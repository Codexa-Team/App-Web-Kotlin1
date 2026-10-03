package com.example.spacepulse.view

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
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
import com.example.spacepulse.model.beans.UpdateIoTDeviceRequest
import com.example.spacepulse.viewmodel.SpaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleIoTDeviceScreen(navController: NavController, spaceViewModel: SpaceViewModel, deviceId: Long) {
    val darkBlue = Color(0xFF2C3E50)
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
    val token = sharedPref.getString("USER_TOKEN", "") ?: ""

    val iotDevices by spaceViewModel.iotDevices.collectAsState()
    val myDevices by spaceViewModel.myIoTDevices.collectAsState()
    val device = iotDevices.find { it.id == deviceId } ?: myDevices.find { it.id == deviceId }

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var editedName by remember { mutableStateOf(device?.name ?: "") }
    var editedSerial by remember { mutableStateOf(device?.serialNumber ?: "") }

    val updateState by spaceViewModel.updateIoTDeviceState.collectAsState()
    val deleteState by spaceViewModel.deleteIoTDeviceState.collectAsState()

    LaunchedEffect(device?.spaceId) {
        val sId = device?.spaceId
        if (sId != null && token.isNotEmpty()) {
            spaceViewModel.fetchIoTDevices(token, sId)
            spaceViewModel.fetchDeviceReading(token, deviceId)
        }
    }

    LaunchedEffect(updateState) {
        if (updateState?.isSuccess == true) {
            showEditDialog = false
            spaceViewModel.resetStates()
        }
    }

    LaunchedEffect(deleteState) {
        if (deleteState?.isSuccess == true) {
            showDeleteDialog = false
            spaceViewModel.resetStates()
            navController.popBackStack()
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar dispositivo", fontWeight = FontWeight.Bold, color = darkBlue) },
            text = { Text("¿Estás seguro de que deseas eliminar este dispositivo?") },
            confirmButton = {
                TextButton(onClick = { 
                    if (device != null) {
                        spaceViewModel.deleteIoTDevice(token, deviceId, device.spaceId)
                    }
                }) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            containerColor = Color.White
        )
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar dispositivo", fontWeight = FontWeight.Bold, color = darkBlue) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editedName, onValueChange = { editedName = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = editedSerial, onValueChange = { editedSerial = it },
                        label = { Text("Número de serie") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (device != null) {
                            spaceViewModel.updateIoTDevice(token, deviceId, device.spaceId, UpdateIoTDeviceRequest(editedName, editedSerial))
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = darkBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Guardar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            containerColor = Color.White
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Dispositivo", fontWeight = FontWeight.Bold, color = darkBlue) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = darkBlue)
                    }
                },
                actions = {
                    IconButton(onClick = { spaceViewModel.fetchDeviceReading(token, deviceId) }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Actualizar", tint = darkBlue)
                    }
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = darkBlue)
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        if (device != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F6F8)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = device.name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = darkBlue)
                                Text(text = "S/N: ${device.serialNumber}", color = Color.Gray, fontSize = 14.sp)
                            }
                            Switch(
                                checked = device.isOn,
                                onCheckedChange = { spaceViewModel.toggleIoTDevice(token, deviceId, device.spaceId) },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = darkBlue)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Tipo: ${device.type}", fontWeight = FontWeight.Medium, color = darkBlue)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Métricas Configuradas", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
                Spacer(modifier = Modifier.height(16.dp))

                InfoRow(label = "Métrica", value = device.metricName ?: "N/A")
                InfoRow(label = "Lectura Actual", value = if (device.value != null) "${device.value} ${device.unit ?: ""}" else "N/A")
                InfoRow(label = "Unidad", value = device.unit ?: "N/A")
                InfoRow(label = "Umbral Mínimo", value = device.minThreshold?.toString() ?: "N/A")
                InfoRow(label = "Umbral Máximo", value = device.maxThreshold?.toString() ?: "N/A")
                
                Spacer(modifier = Modifier.height(32.dp))
                
                if (device.isOn) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Dispositivo operando normalmente",
                            modifier = Modifier.padding(16.dp),
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "El dispositivo está apagado",
                            modifier = Modifier.padding(16.dp),
                            color = Color(0xFFC62828),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontWeight = FontWeight.Medium)
        Text(text = value, color = Color(0xFF2C3E50), fontWeight = FontWeight.Bold)
    }
}