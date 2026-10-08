package com.example.spacepulse.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.spacepulse.R
import com.example.spacepulse.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(navController: NavController, viewModel: AuthViewModel) {
    val context = LocalContext.current

    // Estados del formulario
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("arrendatario") } // "arrendatario" o "arrendador"
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Estado para la foto
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }

    val registerState by viewModel.registerState.collectAsState()
    val darkBlue = Color(0xFF2C3E50)

    LaunchedEffect(registerState) {
        if (registerState != null) {
            isLoading = false
        }
        if (registerState?.isSuccess == true) {
            viewModel.resetStates()
            navController.navigate("login") {
                popUpTo("register") { inclusive = true }
            }
        } else if (registerState?.isFailure == true) {
            errorMessage = registerState?.exceptionOrNull()?.message ?: "Error al registrarse"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Crear cuenta", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = darkBlue)
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.renticar2),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Renticar",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Selección de Tipo de Cuenta
        Text(text = "Tipo de Cuenta:", fontWeight = FontWeight.Bold, color = darkBlue, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { selectedRole = "arrendatario" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selectedRole == "arrendatario") darkBlue else Color.Transparent,
                    contentColor = if (selectedRole == "arrendatario") Color.White else darkBlue
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cliente")
            }

            OutlinedButton(
                onClick = { selectedRole = "arrendador" },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selectedRole == "arrendador") darkBlue else Color.Transparent,
                    contentColor = if (selectedRole == "arrendador") Color.White else darkBlue
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Propietario")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (selectedImageUri != null) Color(0xFF4CAF50) else Color(0xFF4DB7ED)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(if (selectedImageUri != null) "¡Foto seleccionada! ✓" else "Añadir foto de perfil")
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = fullName, onValueChange = { fullName = it },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phone, onValueChange = { phone = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword, onValueChange = { confirmPassword = it },
            label = { Text("Confirmar contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
                    errorMessage = "Por favor completa todos los campos requeridos"
                    return@Button
                }
                if (password != confirmPassword) {
                    errorMessage = "Las contraseñas no coinciden"
                    return@Button
                }
                errorMessage = null
                isLoading = true
                viewModel.registerUser(
                    context = context,
                    imageUri = selectedImageUri,
                    email = email,
                    pass = password,
                    name = fullName,
                    phone = phone,
                    role = selectedRole
                )
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = darkBlue),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Text("Registrarme", fontSize = 18.sp, color = Color.White)
            }
        }

        if (errorMessage != null) {
            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            TextButton(onClick = { navController.navigate("login") }) {
                Text("Ya tengo una cuenta", color = Color.Gray)
            }
        }
    }
}