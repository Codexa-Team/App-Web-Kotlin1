package com.example.spacepulse.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PagosView() {
    val darkBlue = Color(0xFF2C3E50)
    val lightBackground = Color(0xFFE5E7E9)

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        Text(text = "Pagos", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = darkBlue)
        Text(text = "Revisa tus transacciones", fontSize = 16.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { }, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(24.dp), colors = ButtonDefaults.buttonColors(containerColor = darkBlue)) {
                Text("Pendientes", color = Color.White)
            }
            OutlinedButton(onClick = { }, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(24.dp), border = BorderStroke(1.dp, Color.LightGray)) {
                Text("Historial", color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Resumen", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(colors = CardDefaults.cardColors(containerColor = lightBackground), shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "S/. 5,000", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = darkBlue)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Pagado", color = Color.Gray, fontSize = 14.sp)
                }
            }
            Card(colors = CardDefaults.cardColors(containerColor = lightBackground), shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "1", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = darkBlue)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Pendiente", color = Color.Gray, fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Pagos pendientes", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
        Spacer(modifier = Modifier.height(12.dp))

        Card(shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color(0xFFE0E0E0)), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Adelanto del servicio", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = darkBlue)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Remodelación de cocina y sala", color = Color.Gray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "S/ 5,000 - Pendiente", color = Color.Gray, fontSize = 14.sp)
                    }
                    Button(onClick = { }, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = darkBlue)) {
                        Text("Pagar")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Próximos pagos", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
        Spacer(modifier = Modifier.height(12.dp))

        Card(shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color(0xFFE0E0E0)), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Segunda cuota", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = darkBlue)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Pago programado del proyecto", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "S/ 3,000 - Por confirmar", color = Color.Gray, fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}