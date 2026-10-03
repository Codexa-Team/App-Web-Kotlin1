package com.example.spacepulse.view

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import com.example.spacepulse.viewmodel.AuthViewModel
import com.example.spacepulse.viewmodel.SpaceViewModel

@Composable
fun DashboardView(navController: NavController, spaceViewModel: SpaceViewModel, authViewModel: AuthViewModel, onTabSelected: (Int) -> Unit) {
    val context = LocalContext.current
    val darkBlue = Color(0xFF2C3E50)
    val lightBackground = Color(0xFFF8F9FA)

    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
    val fullName = sharedPref.getString("USER_FULL_NAME", "Usuario") ?: "Usuario"
    val firstName = fullName.split(" ").firstOrNull() ?: "Usuario"
    val token = sharedPref.getString("USER_TOKEN", "") ?: ""
    val userId = sharedPref.getString("USER_ID", "") ?: ""

    val spaces by spaceViewModel.spaces.collectAsState()
    val notifications by spaceViewModel.notifications.collectAsState()
    val userProfile by authViewModel.userProfile.collectAsState()
    LaunchedEffect(Unit) {
        if (token.isNotEmpty() && userId.isNotEmpty()) {
            spaceViewModel.fetchSpaces(token, userId)
            spaceViewModel.fetchNotifications(token)
            authViewModel.fetchProfile(token, userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Hola, $firstName", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = darkBlue)
                Text(text = "Revisa tus espacios", fontSize = 16.sp, color = Color.Gray)
            }
            val photoUrl = userProfile?.photo
            if (!photoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Perfil",
                    modifier = Modifier.size(45.dp),
                    tint = Color.LightGray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        val activeSpace = spaces.firstOrNull()

        Card(
            colors = CardDefaults.cardColors(containerColor = lightBackground),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().clickable {
                activeSpace?.let { navController.navigate("detalleEspacio/${it.id}") }
            },
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Proyecto activo", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = activeSpace?.title ?: "No tienes proyectos activos",
                    color = Color.DarkGray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    Surface(shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, darkBlue), color = Color.Transparent) {
                        Text(
                            text = activeSpace?.status ?: "Pendiente",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Accesos rápidos", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickAccessButton(title = "Alertas", modifier = Modifier.weight(1f)) { onTabSelected(2) }
            QuickAccessButton(title = "Espacios", modifier = Modifier.weight(1f)) { onTabSelected(1) }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickAccessButton(title = "Monitoreo", modifier = Modifier.weight(1f)) { onTabSelected(3) }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Alertas recientes", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (notifications.isEmpty()) {
                    Text(text = "No tienes alertas recientes", color = Color.Gray, fontSize = 14.sp)
                } else {
                    Text(text = "Tienes nuevas notificaciones", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = darkBlue)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Revisa tu centro de alertas", color = Color.Gray, fontSize = 13.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun QuickAccessButton(title: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Text(title, color = Color.Black, fontWeight = FontWeight.Medium)
    }
}