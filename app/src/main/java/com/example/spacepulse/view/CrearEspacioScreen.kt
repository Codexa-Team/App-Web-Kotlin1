package com.example.spacepulse.view

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.spacepulse.R
import com.example.spacepulse.viewmodel.SpaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearEspacioScreen(
    navController: NavController,
    spaceViewModel: SpaceViewModel,
    onSuccess: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val darkBlue = Color(0xFF2C3E50)
    val accentBlue = Color(0xFF4DB7ED)

    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var yearText by remember { mutableStateOf("2024") }
    var priceText by remember { mutableStateOf("50.0") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    val createState by spaceViewModel.createVehicleState.collectAsState()
    val isLoading by spaceViewModel.isLoading.collectAsState()

    LaunchedEffect(createState) {
        if (createState?.isSuccess == true) {
            spaceViewModel.resetStates()
            if (onSuccess != null) {
                onSuccess()
            } else {
                navController.popBackStack()
            }
        } else if (createState?.isFailure == true) {
            errorMessage = createState?.exceptionOrNull()?.message ?: "Error al publicar vehículo"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Publicar Vehículo", fontWeight = FontWeight.Bold, color = darkBlue) },
                navigationIcon = {
                    if (navController.previousBackStackEntry != null) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = darkBlue)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text("Detalles del Vehículo", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = darkBlue)
            Text("Ingresa los datos para poner tu auto en alquiler", fontSize = 14.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(20.dp))

            // Selector de foto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.White, shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Foto seleccionada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.DirectionsCar, contentDescription = null, modifier = Modifier.size(50.dp), tint = Color.LightGray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No has seleccionado una foto", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                modifier = Modifier.fillMaxWidth().height(46.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accentBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(if (selectedImageUri != null) "Cambiar foto" else "Seleccionar foto del auto")
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = brand,
                onValueChange = { brand = it },
                label = { Text("Marca (ej. Toyota, Honda, Ford)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = model,
                onValueChange = { model = it },
                label = { Text("Modelo (ej. Corolla, Civic, Ranger)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = yearText,
                onValueChange = { yearText = it },
                label = { Text("Año de fabricación") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = priceText,
                onValueChange = { priceText = it },
                label = { Text("Precio de alquiler por día ($ USD)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (brand.isBlank() || model.isBlank()) {
                        errorMessage = "Por favor indica la marca y el modelo"
                        return@Button
                    }
                    val year = yearText.toIntOrNull() ?: 2024
                    val price = priceText.toDoubleOrNull() ?: 50.0

                    errorMessage = null
                    spaceViewModel.createVehicle(
                        context = context,
                        brand = brand,
                        model = model,
                        year = year,
                        pricePerDay = price,
                        imageUri = selectedImageUri
                    )
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = darkBlue),
                shape = RoundedCornerShape(10.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Publicar Vehículo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}