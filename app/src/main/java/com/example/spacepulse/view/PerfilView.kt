package com.example.spacepulse.view

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.spacepulse.viewmodel.AuthViewModel

@Composable
fun PerfilView(navController: NavController, viewModel: AuthViewModel) {
    val context = LocalContext.current
    val darkBlue = Color(0xFF2C3E50)
    val lightBackground = Color(0xFFE5E7E9)

    val userProfile by viewModel.userProfile.collectAsState()

    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
    val fullName = sharedPref.getString("USER_FULL_NAME", "Usuario") ?: "Usuario"
    val email = sharedPref.getString("USER_EMAIL", "correo@email.com") ?: "correo@email.com"
    val celular = sharedPref.getString("USER_PHONE","+51") ?: "+51 123456789"
    val token = sharedPref.getString("USER_TOKEN", "") ?: ""
    val userId = sharedPref.getString("USER_ID", "") ?: ""

    LaunchedEffect(Unit) {
        if (token.isNotEmpty() && userId.isNotEmpty()) {
            viewModel.fetchProfile(token, userId)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())) {
        Text(text = "Perfil", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = darkBlue)
        Text(text = "Administra tu cuenta", fontSize = 16.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        Card(colors = CardDefaults.cardColors(containerColor = lightBackground), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {

                Box(modifier = Modifier.size(60.dp).background(Color.LightGray, shape = CircleShape), contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(userProfile?.photo)
                            .memoryCachePolicy(CachePolicy.DISABLED)
                            .diskCachePolicy(CachePolicy.DISABLED)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = fullName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
                    Text(text = "Cliente", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Editar perfil", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.clickable { navController.navigate("editarPerfil") })
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Información personal", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
        Spacer(modifier = Modifier.height(12.dp))

        Card(shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color(0xFFE0E0E0)), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Correo electrónico", color = Color.Gray, fontSize = 14.sp)
                Text(text = email, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = darkBlue)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Teléfono", color = Color.Gray, fontSize = 14.sp)
                Text(text = celular, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = darkBlue)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Cuenta", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
        Spacer(modifier = Modifier.height(12.dp))

        Card(shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color(0xFFE0E0E0)), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(modifier = Modifier.fillMaxWidth().clickable { navController.navigate("metodosPago") }.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Métodos de pago", color = darkBlue, fontSize = 16.sp)
                    Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
                }
                HorizontalDivider(color = Color(0xFFE0E0E0))
                Row(modifier = Modifier.fillMaxWidth().clickable { navController.navigate("configuracion") }.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Configuración", color = darkBlue, fontSize = 16.sp)
                    Icon(Icons.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.logout(context); navController.navigate("login") { popUpTo(0) } },
            modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
        ) {
            Text("Cerrar sesión", fontSize = 18.sp, color = Color.White)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}