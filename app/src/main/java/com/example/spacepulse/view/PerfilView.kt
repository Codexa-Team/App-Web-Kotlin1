package com.example.spacepulse.view

import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
    val updateEmailState by viewModel.updateProfileState.collectAsState()
    val updatePasswordState by viewModel.updatePasswordState.collectAsState()

    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
    val fullName = userProfile?.fullName ?: (sharedPref.getString("USER_FULL_NAME", "Usuario") ?: "Usuario")
    val email = userProfile?.email ?: (sharedPref.getString("USER_EMAIL", "correo@email.com") ?: "correo@email.com")
    val userRole = userProfile?.role ?: (sharedPref.getString("USER_ROLE", "ROLE_ARRENDATARIO") ?: "ROLE_ARRENDATARIO")
    val token = sharedPref.getString("USER_TOKEN", "") ?: ""
    val userId = sharedPref.getString("USER_ID", "") ?: ""

    // Estados para editar correo
    var showEditEmailDialog by remember { mutableStateOf(false) }
    var newEmailText by remember { mutableStateOf("") }
    var isUpdatingEmail by remember { mutableStateOf(false) }

    // Estados para cambiar contraseña
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var currentPasswordText by remember { mutableStateOf("") }
    var newPasswordText by remember { mutableStateOf("") }
    var confirmPasswordText by remember { mutableStateOf("") }
    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var isUpdatingPassword by remember { mutableStateOf(false) }

    val roleLabel = if (userRole.contains("arrendador", ignoreCase = true)) "Propietario (Arrendador)" else "Cliente (Arrendatario)"

    LaunchedEffect(Unit) {
        if (token.isNotEmpty() && userId.isNotEmpty()) {
            viewModel.fetchProfile(token, userId)
        }
    }

    // Efecto para respuesta de cambio de correo
    LaunchedEffect(updateEmailState) {
        updateEmailState?.let { result ->
            isUpdatingEmail = false
            result.onSuccess { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                showEditEmailDialog = false
                viewModel.resetUpdateProfileState()
            }.onFailure { err ->
                Toast.makeText(context, err.message ?: "Error al actualizar correo", Toast.LENGTH_LONG).show()
                viewModel.resetUpdateProfileState()
            }
        }
    }

    // Efecto para respuesta de cambio de contraseña
    LaunchedEffect(updatePasswordState) {
        updatePasswordState?.let { result ->
            isUpdatingPassword = false
            result.onSuccess { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                showChangePasswordDialog = false
                currentPasswordText = ""
                newPasswordText = ""
                confirmPasswordText = ""
                viewModel.resetUpdatePasswordState()
            }.onFailure { err ->
                Toast.makeText(context, err.message ?: "Error al cambiar contraseña", Toast.LENGTH_LONG).show()
                viewModel.resetUpdatePasswordState()
            }
        }
    }

    // Modal para editar correo electrónico
    if (showEditEmailDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isUpdatingEmail) showEditEmailDialog = false
            },
            title = {
                Text(
                    text = "Editar Correo Electrónico",
                    fontWeight = FontWeight.Bold,
                    color = darkBlue
                )
            },
            text = {
                Column {
                    Text(
                        text = "Ingresa tu nueva dirección de correo electrónico:",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newEmailText,
                        onValueChange = { newEmailText = it },
                        label = { Text("Correo Electrónico") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUpdatingEmail
                    )
                    if (isUpdatingEmail) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = darkBlue,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardando en la base de datos...", fontSize = 13.sp, color = darkBlue)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val trimmed = newEmailText.trim()
                        if (trimmed.isEmpty() || !trimmed.contains("@")) {
                            Toast.makeText(context, "Ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show()
                        } else {
                            isUpdatingEmail = true
                            viewModel.updateEmail(context, trimmed)
                        }
                    },
                    enabled = !isUpdatingEmail,
                    colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEditEmailDialog = false },
                    enabled = !isUpdatingEmail
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Modal para cambiar contraseña
    if (showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isUpdatingPassword) showChangePasswordDialog = false
            },
            title = {
                Text(
                    text = "Cambiar Contraseña",
                    fontWeight = FontWeight.Bold,
                    color = darkBlue
                )
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = "Ingresa tu contraseña actual y define tu nueva contraseña:",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = currentPasswordText,
                        onValueChange = { currentPasswordText = it },
                        label = { Text("Contraseña Actual") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUpdatingPassword,
                        visualTransformation = if (showCurrentPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showCurrentPassword = !showCurrentPassword }) {
                                Icon(
                                    imageVector = if (showCurrentPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = if (showCurrentPassword) "Ocultar" else "Mostrar"
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = newPasswordText,
                        onValueChange = { newPasswordText = it },
                        label = { Text("Nueva Contraseña") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUpdatingPassword,
                        visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showNewPassword = !showNewPassword }) {
                                Icon(
                                    imageVector = if (showNewPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = if (showNewPassword) "Ocultar" else "Mostrar"
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = confirmPasswordText,
                        onValueChange = { confirmPasswordText = it },
                        label = { Text("Confirmar Nueva Contraseña") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUpdatingPassword,
                        visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                Icon(
                                    imageVector = if (showConfirmPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = if (showConfirmPassword) "Ocultar" else "Mostrar"
                                )
                            }
                        }
                    )

                    if (isUpdatingPassword) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = darkBlue,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardando en la base de datos...", fontSize = 13.sp, color = darkBlue)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val current = currentPasswordText.trim()
                        val newPass = newPasswordText.trim()
                        val confirm = confirmPasswordText.trim()

                        if (current.isEmpty()) {
                            Toast.makeText(context, "Ingresa tu contraseña actual", Toast.LENGTH_SHORT).show()
                        } else if (newPass.isEmpty()) {
                            Toast.makeText(context, "Ingresa la nueva contraseña", Toast.LENGTH_SHORT).show()
                        } else if (newPass.length < 6) {
                            Toast.makeText(context, "La nueva contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                        } else if (newPass != confirm) {
                            Toast.makeText(context, "Las nuevas contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                        } else {
                            isUpdatingPassword = true
                            viewModel.updatePassword(context, current, newPass)
                        }
                    },
                    enabled = !isUpdatingPassword,
                    colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showChangePasswordDialog = false },
                    enabled = !isUpdatingPassword
                ) {
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
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Correo Electrónico", color = Color.Gray, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = email, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = darkBlue)
                    }
                    IconButton(
                        onClick = {
                            newEmailText = email
                            showEditEmailDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Editar correo",
                            tint = darkBlue
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFF5F5F5))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Contraseña", color = Color.Gray, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "••••••••••••", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = darkBlue)
                    }
                    IconButton(
                        onClick = {
                            currentPasswordText = ""
                            newPasswordText = ""
                            confirmPasswordText = ""
                            showCurrentPassword = false
                            showNewPassword = false
                            showConfirmPassword = false
                            showChangePasswordDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Cambiar contraseña",
                            tint = darkBlue
                        )
                    }
                }
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
                        .clickable {
                            currentPasswordText = ""
                            newPasswordText = ""
                            confirmPasswordText = ""
                            showCurrentPassword = false
                            showNewPassword = false
                            showConfirmPassword = false
                            showChangePasswordDialog = true
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Lock, contentDescription = null, tint = darkBlue, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Cambiar Contraseña", color = darkBlue, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
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
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
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