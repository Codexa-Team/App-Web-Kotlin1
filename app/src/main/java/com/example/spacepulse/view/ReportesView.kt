package com.example.spacepulse.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReportesView() {
    val darkBlue = Color(0xFF2C3E50)
    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        Text(text = "Reportes", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = darkBlue)
        Text(text = "Consulta tus informes", fontSize = 16.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = searchQuery, onValueChange = { searchQuery = it },
            placeholder = { Text("Buscar reporte..") }, trailingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray, focusedBorderColor = darkBlue)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Categorías", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Button(onClick = { }, shape = RoundedCornerShape(24.dp), colors = ButtonDefaults.buttonColors(containerColor = darkBlue)) {
                    Text("Todos", color = Color.White)
                }
            }
            item {
                OutlinedButton(onClick = { }, shape = RoundedCornerShape(24.dp), border = BorderStroke(1.dp, Color.LightGray)) {
                    Text("Avance", color = Color.Gray)
                }
            }
            item {
                OutlinedButton(onClick = { }, shape = RoundedCornerShape(24.dp), border = BorderStroke(1.dp, Color.LightGray)) {
                    Text("Costos", color = Color.Gray)
                }
            }
            item {
                OutlinedButton(onClick = { }, shape = RoundedCornerShape(24.dp), border = BorderStroke(1.dp, Color.LightGray)) {
                    Text("Incidencias", color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color(0xFFE0E0E0)), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Reporte de avance", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = darkBlue)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Remodelación de cocina y sala", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Generado: 10 mayo", color = Color.Gray, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color(0xFFE0E0E0)), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Reporte de costos", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = darkBlue)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Presupuesto estimado: S/ 15,000", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Actualizado: 10 mayo", color = Color.Gray, fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}