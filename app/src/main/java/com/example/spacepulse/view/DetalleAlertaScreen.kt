package com.example.spacepulse.view

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.spacepulse.viewmodel.SpaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleAlertaScreen(
    navController: NavController,
    spaceViewModel: SpaceViewModel,
    notificationId: Long
) {
    val darkBlue = Color(0xFF2C3E50)
    val secondaryText = Color(0xFF5F6B76)
    val borderGray = Color(0xFFE0E0E0)

    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
    val token = sharedPref.getString("USER_TOKEN", "") ?: ""

    val notifications by spaceViewModel.notifications.collectAsState()
    val notification = notifications.find { it.id == notificationId }

    LaunchedEffect(notificationId) {
        if (token.isNotEmpty() && notifications.isEmpty()) {
            spaceViewModel.fetchNotifications(token)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Detalle de alerta",
                            fontWeight = FontWeight.Bold,
                            color = darkBlue,
                            fontSize = 22.sp
                        )
                        Text(
                            "Información de la notificación",
                            color = secondaryText,
                            fontSize = 14.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = darkBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->

        if (notification == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No se encontró la alerta",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkBlue
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
                ) {
                    Text("Volver", color = Color.White)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF8F9FA)
                    ),
                    border = BorderStroke(1.dp, borderGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = null,
                            tint = darkBlue
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = notification.title ?: "Notificación",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = darkBlue
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (notification.isRead == true) "Leída" else "No leída",
                                fontSize = 13.sp,
                                color = secondaryText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Mensaje",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = darkBlue
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, borderGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = notification.message ?: "Sin mensaje",
                        modifier = Modifier.padding(16.dp),
                        color = secondaryText,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Información",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = darkBlue
                )

                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, borderGray),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Espacio: ${notification.spaceId ?: "No asociado"}",
                            color = secondaryText,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "Fecha: ${formatNotificationDate(notification.createdAt)}",
                            color = secondaryText,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            "Estado: ${if (notification.isRead == true) "Leída" else "No leída"}",
                            color = secondaryText,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        if (token.isNotEmpty()) {
                            spaceViewModel.markNotificationAsRead(token, notificationId) {
                                navController.popBackStack()
                            }
                        } else {
                            navController.popBackStack()
                        }
                    },
                    enabled = notification.isRead != true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = darkBlue,
                        disabledContainerColor = Color(0xFFAAB3BC)
                    )
                ) {
                    Text(
                        text = if (notification.isRead == true) {
                            "Alerta leída"
                        } else {
                            "Marcar como leída"
                        },
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}