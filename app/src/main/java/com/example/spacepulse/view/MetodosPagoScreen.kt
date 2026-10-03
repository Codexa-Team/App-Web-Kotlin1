package com.example.spacepulse.view

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Payments
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
import com.example.spacepulse.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetodosPagoScreen(navController: NavController, viewModel: AuthViewModel) {
    val darkBlue = Color(0xFF2C3E50)
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
    val token = sharedPref.getString("USER_TOKEN", "") ?: ""
    val userId = sharedPref.getString("USER_ID", "") ?: ""

    val userProfile by viewModel.userProfile.collectAsState()

    LaunchedEffect(Unit) {
        if (token.isNotEmpty() && userId.isNotEmpty()) {
            viewModel.fetchProfile(token, userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Métodos de pago", fontWeight = FontWeight.Bold, color = darkBlue) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = darkBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("agregarPago") },
                containerColor = darkBlue,
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar")
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        val payments = userProfile?.paymentMethods ?: emptyList()

        if (payments.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Filled.Payments, contentDescription = null, modifier = Modifier.size(100.dp), tint = Color.LightGray)
                Spacer(modifier = Modifier.height(24.dp))
                Text("No tienes un método de pago asociado", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = darkBlue, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { navController.navigate("agregarPago") },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
                ) {
                    Text("Agregar medio de pago")
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp)) {
                items(payments) { payment ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(48.dp).background(Color(0xFFF5F6F8), shape = CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.CreditCard, contentDescription = null, tint = darkBlue)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(text = if (!payment.lastFour.isNullOrEmpty()) "**** **** **** ${payment.lastFour}" else "**** **** **** ****", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = darkBlue)
                                Text(text = "${payment.type} - Expira: ${payment.expiry}", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}