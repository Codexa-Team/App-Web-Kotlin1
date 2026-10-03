package com.example.spacepulse.view

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.spacepulse.viewmodel.SpaceViewModel

@Composable
fun EspaciosView(navController: NavController, spaceViewModel: SpaceViewModel) {
    val darkBlue = Color(0xFF2C3E50)
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
    val token = sharedPref.getString("USER_TOKEN", "") ?: ""
    val userId = sharedPref.getString("USER_ID", "") ?: ""

    val spaces by spaceViewModel.spaces.collectAsState()

    val filteredSpaces = spaces.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.location.contains(searchQuery, ignoreCase = true)
    }

    LaunchedEffect(Unit) {
        if (token.isNotEmpty() && userId.isNotEmpty()) {
            spaceViewModel.fetchSpaces(token, userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Mis Espacios", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = darkBlue)
                Text(text = "Administra tus ambientes", fontSize = 16.sp, color = Color.Gray)
            }
            IconButton(
                onClick = { navController.navigate("crearEspacio") },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFE5E7E9), shape = CircleShape)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar", tint = darkBlue)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Buscar espacio...") },
            trailingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar", tint = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = darkBlue
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (spaces.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = Color(0xFFB0BEC5)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Aún no tienes espacios registrados", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = darkBlue)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Crea tu primer espacio para iniciar un\nproyecto",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { navController.navigate("crearEspacio") },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
                ) {
                    Text("Crear espacio", fontSize = 16.sp, color = Color.White)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredSpaces) { space ->
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .clickable { navController.navigate("detalleEspacio/${space.id}") }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = space.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = darkBlue)
                                
                                val statusText = when (space.status.uppercase()) {
                                    "0", "PUBLISHED", "PUBLICADO" -> "Publicado"
                                    "1", "ACCEPTED", "ACEPTADO" -> "Aceptado"
                                    "2", "IN_PROGRESS", "EN_PROCESO" -> "En Progreso"
                                    "3", "COMPLETED", "COMPLETADO", "FINISHED", "FINALIZADO" -> "Completado"
                                    "4", "CANCELLED", "CANCELADO" -> "Cancelado"
                                    else -> space.status
                                }
                                
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = when (space.status.uppercase()) {
                                        "3", "COMPLETED", "COMPLETADO", "FINISHED", "FINALIZADO" -> Color(0xFFE8F5E9)
                                        "4", "CANCELLED", "CANCELADO" -> Color(0xFFFFEBEE)
                                        "2", "IN_PROGRESS", "EN_PROCESO" -> Color(0xFFE3F2FD)
                                        else -> Color(0xFFF5F5F5)
                                    },
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text(
                                        text = statusText,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (space.status.uppercase()) {
                                            "3", "COMPLETED", "COMPLETADO", "FINISHED", "FINALIZADO" -> Color(0xFF2E7D32)
                                            "4", "CANCELLED", "CANCELADO" -> Color(0xFFC62828)
                                            "2", "IN_PROGRESS", "EN_PROCESO" -> Color(0xFF1976D2)
                                            else -> Color.DarkGray
                                        }
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = space.location, color = Color.Gray, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            AsyncImage(
                                model = space.images.firstOrNull()?.ifBlank { null },
                                contentDescription = "Imagen del espacio",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.Center,
                                onLoading = {
                                    android.util.Log.d("IMG_DEBUG", "Cargando...")
                                },
                                onSuccess = {
                                    android.util.Log.d("IMG_DEBUG", "¡Éxito!")
                                },
                                onError = { error ->
                                    android.util.Log.e(
                                        "IMG_DEBUG",
                                        "Error cargando: ${error.result.throwable.message}"
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}