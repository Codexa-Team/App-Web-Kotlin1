package com.example.spacepulse.view

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfilScreen(navController: NavController) {
    val context = LocalContext.current
    val darkBlue = Color(0xFF2C3E50)

    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)

    var fullName by remember { mutableStateOf(sharedPref.getString("USER_FULL_NAME", "") ?: "") }
    var email by remember { mutableStateOf(sharedPref.getString("USER_EMAIL", "") ?: "") }
    var phone by remember { mutableStateOf("+51 987 654 321") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Editar perfil", fontWeight = FontWeight.Bold, color = darkBlue, fontSize = 22.sp)
                        Text("Actualiza tus datos", color = Color.Gray, fontSize = 14.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = darkBlue)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Opciones", tint = Color.Gray)
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
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Nombre completo") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo electrónico") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    with(sharedPref.edit()) {
                        putString("USER_FULL_NAME", fullName)
                        putString("USER_EMAIL", email)
                        apply()
                    }
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
            ) {
                Text("Guardar cambios", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}