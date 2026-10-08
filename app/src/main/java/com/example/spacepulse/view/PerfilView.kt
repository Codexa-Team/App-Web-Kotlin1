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
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.spacepulse.R
import com.example.spacepulse.model.client.RetrofitClient
import com.example.spacepulse.viewmodel.AuthViewModel

@Composable
fun PerfilView(navController: NavController, viewModel: AuthViewModel) {
    val context = LocalContext.current
    val darkBlue = Color(0xFF2C3E50)
    val lightBackground = Color(0xFFF8F9FA)

    val userProfile by viewModel.userProfile.collectAsState()

    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
    val fullName = sharedPref.getString("USER_FULL_NAME", "Usuario") ?: "Usuario"
    val email = sharedPref.getString("USER_EMAIL", "correo@email.com") ?: "correo@email.com"
    val userRole = sharedPref.getString("USER_ROLE", "ROLE_ARRENDATARIO") ?: "ROLE_ARRENDATARIO"
    val token = sharedPref.getString("USER_TOKEN", "") ?: ""
    val userId = sharedPref.getString("USER_ID", "") ?: ""

    var showServerDialog by remember { mutableStateOf(false) }
    var currentBaseUrl by remember { mutableStateOf(RetrofitClient.getBaseUrl()) }

    val roleLabel = if (userRole.contains("arrendador", ignoreCase = true)) "Propietario (Arrendador)" else "Cliente (Arrendatario)"

    LaunchedEffect(Unit) {
        if (token.isNotEmpty() && userId.isNotEmpty()) {
            viewModel.fetchProfile(token, userId)
        }
    }

    // Modal para cambiar IP del Servidor
    if (showServerDialog) {
        AlertDialog(
            onDismissRequest = { showServerDialog = false },
            title = { Text("Configurar Servidor Backend", fontWeight = FontWeight.Bold, color = darkBlue) },
            text = {
                Column {
                    Text("Selecciona o escribe la dirección IP de tu backend Spring Boot:", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {
                            currentBaseUrl = RetrofitClient.EMULATOR_BASE_URL
                            RetrofitClient.setBaseUrl(currentBaseUrl)
                            showServerDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Emulador Android Studio (10.0.2.2:8080)")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            currentBaseUrl = RetrofitClient.LAN_BASE_URL
                            RetrofitClient.setBaseUrl(currentBaseUrl)
                            showServerDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Celular Físico Wi-Fi (192.168.18.85:8080)")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = currentBaseUrl,
                        onValueChange = { currentBaseUrl = it },
                        label = { Text("URL Personalizada") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        RetrofitClient.setBaseUrl(currentBaseUrl)
                        showServerDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showServerDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Mi Perfil", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = darkBlue)
        Text(text = "Detalles de tu cuenta Renticar", fontSize = 15.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        // Tarjeta de usuario
        Card(
            colors = CardDefaults.cardColors(containerColor = lightBackground),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.White, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.renticar2),
                        contentDescription = "Logo",
                        modifier = Modifier.size(50.dp).clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(text = fullName, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = darkBlue)
                    Surface(
                        color = darkBlue.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = roleLabel,
                            color = darkBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Información Personal", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color(0xFFE5E7E9)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Correo Electrónico", color = Color.Gray, fontSize = 13.sp)
                Text(text = email, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = darkBlue)

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFF5F5F5))
                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "ID de Usuario", color = Color.Gray, fontSize = 13.sp)
                Text(text = userId.ifEmpty { "1" }, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = darkBlue)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Tarjeta para alternar vista (Propietario / Cliente)
        Card(
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color(0xFF3498DB).copy(alpha = 0.4f)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEBF5FB)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val currentRole = sharedPref.getString("USER_ROLE", "ROLE_ARRENDATARIO") ?: "ROLE_ARRENDATARIO"
                    val newRole = if (currentRole.contains("arrendador", ignoreCase = true)) "ROLE_ARRENDATARIO" else "ROLE_ARRENDADOR"
                    sharedPref.edit().putString("USER_ROLE", newRole).apply()
                    navController.navigate("clientHome") {
                        popUpTo("clientHome") { inclusive = true }
                    }
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Refresh, contentDescription = null, tint = Color(0xFF2980B9), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Cambiar Modo de Vista",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B4F72),
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (userRole.contains("arrendador", ignoreCase = true))
                                "Modo Propietario activo ➔ Toca para modo Cliente"
                            else
                                "Modo Cliente activo ➔ Toca para modo Propietario",
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }
                }
                Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF2980B9))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Ajustes del Sistema", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color(0xFFE5E7E9)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showServerDialog = true }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Dns, contentDescription = null, tint = darkBlue, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Servidor Backend IP", color = darkBlue, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Text(text = RetrofitClient.getBaseUrl(), color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                    Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
                }

                HorizontalDivider(color = Color(0xFFF5F5F5))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("configuracion") }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Settings, contentDescription = null, tint = darkBlue, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Configuración General", color = darkBlue, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                    Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.logout(context)
                navController.navigate("login") { popUpTo(0) }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE74C3C))
        ) {
            Text("Cerrar sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}