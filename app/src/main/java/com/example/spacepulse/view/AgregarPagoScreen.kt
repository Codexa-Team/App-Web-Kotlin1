package com.example.spacepulse.view

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.spacepulse.model.beans.AddPaymentMethodRequest
import com.example.spacepulse.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarPagoScreen(navController: NavController, viewModel: AuthViewModel) {
    val darkBlue = Color(0xFF2C3E50)
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
    val token = sharedPref.getString("USER_TOKEN", "") ?: ""
    val userId = sharedPref.getString("USER_ID", "") ?: ""

    val paymentTypes = listOf(
        "Visa" to "VISA",
        "MasterCard" to "MC",
        "Dinners" to "DINNERS"
    )

    var selectedType by remember { mutableStateOf(paymentTypes[0]) }
    var expanded by remember { mutableStateOf(false) }

    var number by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val paymentState by viewModel.paymentState.collectAsState()

    LaunchedEffect(paymentState) {
        if (paymentState != null) {
            isLoading = false
            if (paymentState?.isSuccess == true) {
                viewModel.resetStates()
                navController.popBackStack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo medio de pago", fontWeight = FontWeight.Bold, color = darkBlue) },
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
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp).verticalScroll(rememberScrollState())
        ) {
            Text("Tipo de medio de pago", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedType.first,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    paymentTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.first) },
                            onClick = {
                                selectedType = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = number,
                onValueChange = { if (it.length <= 16 && it.all { char -> char.isDigit() }) number = it },
                label = { Text("Número de tarjeta / cuenta") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = expiry,
                    onValueChange = { input ->
                        val cleanInput = input.filter { it.isDigit() }
                        if (cleanInput.length <= 4) {
                            expiry = if (cleanInput.length >= 3) {
                                cleanInput.substring(0, 2) + "/" + cleanInput.substring(2)
                            } else {
                                cleanInput
                            }
                        }
                    },
                    label = { Text("Expiración (MM/YY)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = cvv,
                    onValueChange = { if (it.length <= 3 && it.all { char -> char.isDigit() }) cvv = it },
                    label = { Text("CVV") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = {
                    isLoading = true
                    val request = AddPaymentMethodRequest(type = selectedType.second, number = number, expiry = expiry, cvv = cvv)
                    viewModel.addPaymentMethod(token, userId, request)
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = darkBlue),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Guardar medio de pago")
                }
            }
        }
    }
}