package com.example.spacepulse.view

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.spacepulse.model.beans.CreateSpaceRequest
import com.example.spacepulse.viewmodel.SpaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun CrearEspacioScreen(navController: NavController, spaceViewModel: SpaceViewModel) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }
    val context = LocalContext.current
    val darkBlue = Color(0xFF2C3E50)
    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
    val token = sharedPref.getString("USER_TOKEN", "") ?: ""
    val userId = sharedPref.getString("USER_ID", "") ?: ""

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }

    val currencies = listOf("PEN", "USD", "EUR")
    var currency by remember { mutableStateOf(currencies[0]) }
    var expandedCurrency by remember { mutableStateOf(false) }
    var hasIot by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var showIncompleteDialog by remember { mutableStateOf(false) }

    val createState by spaceViewModel.createSpaceState.collectAsState()

    LaunchedEffect(createState) {
        if (createState != null) {
            isLoading = false
        }
        if (createState?.isSuccess == true) {
            spaceViewModel.resetStates()
            navController.popBackStack()
        }
    }

    if (showIncompleteDialog) {
        AlertDialog(
            onDismissRequest = { showIncompleteDialog = false },
            title = { Text("Datos incompletos", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = darkBlue) },
            text = { Text("Completa los campos obligatorios\npara continuar", color = Color.Gray, fontSize = 16.sp) },
            confirmButton = {
                Button(
                    onClick = { showIncompleteDialog = false },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
                ) {
                    Text("Entendido", color = Color.White)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo espacio", fontWeight = FontWeight.Bold, color = darkBlue, fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = darkBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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
            Text(text = "Datos principales", fontSize = 16.sp, color = Color.Gray)
            Button(
                onClick = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (selectedImageUri != null) Color(0xFF4CAF50) else darkBlue)
            ) {
                Text(if (selectedImageUri != null) "¡Foto del espacio seleccionada! ✓" else "Añadir foto del espacio")
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Título del proyecto") },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = location, onValueChange = { location = it },
                label = { Text("Ubicación") },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = area, onValueChange = { area = it },
                label = { Text("Área m2") },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = budget, onValueChange = { budget = it },
                    label = { Text("Presupuesto estimado") },
                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = expandedCurrency,
                    onExpandedChange = { expandedCurrency = !expandedCurrency },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = currency,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Moneda") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCurrency) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCurrency,
                        onDismissRequest = { expandedCurrency = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        currencies.forEach { selection ->
                            DropdownMenuItem(
                                text = { Text(selection) },
                                onClick = {
                                    currency = selection
                                    expandedCurrency = false
                                }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Habilitar tecnología IoT", fontSize = 16.sp, color = darkBlue)
                Switch(
                    checked = hasIot,
                    onCheckedChange = { hasIot = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = darkBlue)
                )
            }
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (title.isEmpty() || description.isEmpty() || location.isEmpty() || area.isEmpty() || budget.isEmpty()) {
                        showIncompleteDialog = true
                    } else {
                        isLoading = true
                        val request = CreateSpaceRequest(
                            homeownerId = userId,
                            title = title,
                            description = description,
                            location = location,
                            spaceType = "0",
                            dimensionsSquareMeters = area.toDoubleOrNull() ?: 0.0,
                            estimatedBudget = budget.toDoubleOrNull() ?: 0.0,
                            currency = currency,
                            hasIot = hasIot,
                            images = emptyList()
                        )

                        spaceViewModel.createSpace(
                            context = context,
                            imageUri = selectedImageUri,
                            token = token,
                            userId = userId,
                            request = request
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = darkBlue),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Continuar", fontSize = 18.sp, color = Color.White)
                }
            }
        }
    }
}