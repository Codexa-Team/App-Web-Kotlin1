package com.example.spacepulse.view

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.spacepulse.model.beans.CreateIoTDeviceRequest
import com.example.spacepulse.viewmodel.SpaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarIoTDeviceScreen(navController: NavController, spaceViewModel: SpaceViewModel, spaceId: Long) {
    val darkBlue = Color(0xFF2C3E50)
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
    val token = sharedPref.getString("USER_TOKEN", "") ?: ""

    var name by remember { mutableStateOf("") }
    var serialNumber by remember { mutableStateOf("") }
    
    val types = listOf("HUMIDITY", "TEMPERATURE", "VOLTAGE", "LOAD", "AIR_QUALITY", "OTHER")
    var selectedType by remember { mutableStateOf(types[0]) }
    var expanded by remember { mutableStateOf(false) }

    var customMetricName by remember { mutableStateOf("") }
    var customUnit by remember { mutableStateOf("") }
    var customMinThreshold by remember { mutableStateOf("") }
    var customMaxThreshold by remember { mutableStateOf("") }

    val addState by spaceViewModel.addIoTDeviceState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(selectedType) {
        when (selectedType) {
            "HUMIDITY" -> {
                customMetricName = "Humedad"
                customUnit = "%"
                customMinThreshold = "30"
                customMaxThreshold = "60"
            }
            "TEMPERATURE" -> {
                customMetricName = "Temperatura"
                customUnit = "°C"
                customMinThreshold = "18"
                customMaxThreshold = "25"
            }
            "VOLTAGE" -> {
                customMetricName = "Voltaje"
                customUnit = "V"
                customMinThreshold = "110"
                customMaxThreshold = "220"
            }
            "LOAD" -> {
                customMetricName = "Carga"
                customUnit = "kg"
                customMinThreshold = "0"
                customMaxThreshold = "100"
            }
            "AIR_QUALITY" -> {
                customMetricName = "Calidad de Aire"
                customUnit = "AQI"
                customMinThreshold = "0"
                customMaxThreshold = "50"
            }
            "OTHER" -> {
                customMetricName = ""
                customUnit = ""
                customMinThreshold = ""
                customMaxThreshold = ""
            }
        }
    }

    LaunchedEffect(addState) {
        if (addState != null) {
            isLoading = false
            if (addState?.isSuccess == true) {
                spaceViewModel.resetStates()
                navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Dispositivo", fontWeight = FontWeight.Bold, color = darkBlue) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = darkBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Nombre del dispositivo") },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = serialNumber, onValueChange = { serialNumber = it },
                label = { Text("Número de serie") },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de dispositivo") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    types.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                selectedType = type
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text("Configuración de métricas", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = customMetricName, 
                onValueChange = { if (selectedType == "OTHER") customMetricName = it },
                label = { Text("Nombre de la métrica") },
                modifier = Modifier.fillMaxWidth(), 
                shape = RoundedCornerShape(8.dp),
                readOnly = selectedType != "OTHER"
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = customUnit, 
                onValueChange = { if (selectedType == "OTHER") customUnit = it },
                label = { Text("Unidad de medida") },
                modifier = Modifier.fillMaxWidth(), 
                shape = RoundedCornerShape(8.dp),
                readOnly = selectedType != "OTHER"
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = customMinThreshold, 
                    onValueChange = { if (selectedType == "OTHER") customMinThreshold = it },
                    label = { Text("Mínimo") },
                    modifier = Modifier.weight(1f), 
                    shape = RoundedCornerShape(8.dp),
                    readOnly = selectedType != "OTHER"
                )
                OutlinedTextField(
                    value = customMaxThreshold, 
                    onValueChange = { if (selectedType == "OTHER") customMaxThreshold = it },
                    label = { Text("Máximo") },
                    modifier = Modifier.weight(1f), 
                    shape = RoundedCornerShape(8.dp),
                    readOnly = selectedType != "OTHER"
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    isLoading = true
                    val request = CreateIoTDeviceRequest(
                        spaceId = spaceId,
                        type = selectedType,
                        name = name,
                        serialNumber = serialNumber,
                        metricName = customMetricName,
                        unit = customUnit,
                        minThreshold = customMinThreshold.toDoubleOrNull() ?: 0.0,
                        maxThreshold = customMaxThreshold.toDoubleOrNull() ?: 0.0
                    )
                    spaceViewModel.addIoTDevice(token, request)
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = darkBlue),
                enabled = !isLoading && name.isNotEmpty() && serialNumber.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Guardar Dispositivo", fontSize = 18.sp, color = Color.White)
                }
            }

            if (addState?.isFailure == true) {
                Text(text = addState?.exceptionOrNull()?.message ?: "Error al guardar el dispositivo", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}