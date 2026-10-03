package com.example.spacepulse.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(navController: NavController) {
    val darkBlue = Color(0xFF2C3E50)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración", fontWeight = FontWeight.Bold, color = darkBlue) },
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
            ConfigSectionTitle("Notificaciones")
            ConfigItem(Icons.Filled.Notifications, "Notificaciones Push", true)
            ConfigItem(Icons.Filled.Email, "Correos electrónicos", false)

            Spacer(modifier = Modifier.height(24.dp))
            ConfigSectionTitle("Privacidad")
            ConfigItem(Icons.Filled.Lock, "Privacidad de datos", null)
            ConfigItem(Icons.Filled.Security, "Seguridad de la cuenta", null)

            Spacer(modifier = Modifier.height(24.dp))
            ConfigSectionTitle("General")
            ConfigItem(Icons.Filled.Language, "Idioma", null)
            ConfigItem(Icons.Filled.Brightness4, "Tema oscuro", false)

            Spacer(modifier = Modifier.height(24.dp))
            ConfigSectionTitle("Soporte")
            ConfigItem(Icons.Filled.Help, "Ayuda y soporte", null)
            ConfigItem(Icons.Filled.Info, "Acerca de SpacePulse", null)
        }
    }
}

@Composable
fun ConfigSectionTitle(title: String) {
    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 12.dp))
}

@Composable
fun ConfigItem(icon: ImageVector, title: String, switchState: Boolean?) {
    var checked by remember { mutableStateOf(switchState ?: false) }
    Row(
        modifier = Modifier.fillMaxWidth().height(56.dp).clickable(enabled = switchState == null) {},
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFF2C3E50), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, fontSize = 16.sp, color = Color.Black)
        }
        if (switchState != null) {
            Switch(
                checked = checked,
                onCheckedChange = { checked = it },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF2C3E50))
            )
        } else {
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}