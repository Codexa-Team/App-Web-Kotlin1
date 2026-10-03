package com.example.spacepulse.view

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Search
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
import com.example.spacepulse.model.beans.NotificationResponse
import com.example.spacepulse.viewmodel.SpaceViewModel

@Composable
fun MonitoreoView(navController: NavController, spaceViewModel: SpaceViewModel) {
    val darkBlue = Color(0xFF2C3E50)
    val secondaryText = Color(0xFF5F6B76)
    val softGray = Color(0xFFF8F9FA)
    val borderGray = Color(0xFFE0E0E0)

    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
    val token = sharedPref.getString("USER_TOKEN", "") ?: ""

    val notifications by spaceViewModel.notifications.collectAsState()
    val myDevices by spaceViewModel.myIoTDevices.collectAsState()

    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Todas") }

    LaunchedEffect(Unit) { if (token.isNotEmpty()) { spaceViewModel.fetchNotifications(token) }
    }

    val filteredNotifications = notifications
        .filter { notification ->
            when (selectedFilter) {
                "No leídas" -> notification.isRead != true
                "Leídas" -> notification.isRead == true
                else -> true
            }
        }
        .filter { notification ->
            val query = searchText.trim().lowercase()
            if (query.isBlank()) {
                true
            } else {
                val title = notification.title.orEmpty().lowercase()
                val message = notification.message.orEmpty().lowercase()
                title.contains(query) || message.contains(query)
            }
        }
        .sortedWith(
            compareBy<NotificationResponse> { if (it.isRead == true) 1 else 0 }
                .thenByDescending { it.createdAt.orEmpty() }
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Alertas",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = darkBlue
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Eventos recientes",
            fontSize = 14.sp,
            color = secondaryText
        )

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = {
                Text(
                    text = "Buscar alerta...",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Buscar",
                    tint = Color.Gray
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = darkBlue,
                unfocusedBorderColor = borderGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Filtros",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = darkBlue
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            NotificationFilterChip(
                text = "Todas",
                selected = selectedFilter == "Todas",
                onClick = { selectedFilter = "Todas" }
            )

            NotificationFilterChip(
                text = "No leídas",
                selected = selectedFilter == "No leídas",
                onClick = { selectedFilter = "No leídas" }
            )

            NotificationFilterChip(
                text = "Leídas",
                selected = selectedFilter == "Leídas",
                onClick = { selectedFilter = "Leídas" }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        when {
            notifications.isEmpty() -> {
                EmptyNotificationsState(
                    title = "No tienes alertas activas",
                    description = "Todo está funcionando correctamente"
                )
            }

            filteredNotifications.isEmpty() -> {
                EmptyNotificationsState(
                    title = "No se encontraron alertas",
                    description = "Prueba con otro filtro o búsqueda"
                )
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredNotifications) { notification ->
                        NotificationCard(
                            notification = notification,
                            onClick = {
                                val id = notification.id
                                if (id != null) {
                                    navController.navigate("detalleAlerta/$id")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val darkBlue = Color(0xFF2C3E50)
    val borderGray = Color(0xFFE0E0E0)

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) darkBlue else Color.White
        ),
        border = if (selected) null else BorderStroke(1.dp, borderGray),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (selected) Color.White else Color.Gray
        )
    }
}

@Composable
private fun NotificationCard(
    notification: NotificationResponse,
    onClick: () -> Unit
) {
    val darkBlue = Color(0xFF2C3E50)
    val secondaryText = Color(0xFF5F6B76)
    val borderGray = Color(0xFFE0E0E0)
    val unreadColor = Color(0xFFD4A373)

    val isUnread = notification.isRead != true
    val isCritical = isCriticalNotification(notification)

    val cardBackground = when {
        isCritical -> Color(0xFFFFF5F5)
        isUnread -> Color(0xFFFFFBF5)
        else -> Color.White
    }

    val cardBorder = when {
        isCritical -> Color(0xFFC96A6A)
        isUnread -> unreadColor
        else -> borderGray
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        border = BorderStroke(1.dp, cardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = null,
                tint = when {
                    isCritical -> Color(0xFFC96A6A)
                    isUnread -> unreadColor
                    else -> Color.Gray
                },
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title ?: "Notificación",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkBlue
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = notification.message ?: "Sin mensaje",
                    fontSize = 13.sp,
                    color = secondaryText,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formatNotificationDate(notification.createdAt),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            if (isUnread) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .padding(top = 2.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (isCritical) Color(0xFFC96A6A) else unreadColor,
                        modifier = Modifier.fillMaxSize()
                    ) {}
                }
            }
        }
    }
}

@Composable
private fun EmptyNotificationsState(
    title: String,
    description: String
) {
    val darkBlue = Color(0xFF2C3E50)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.NotificationsNone,
            contentDescription = null,
            modifier = Modifier.size(76.dp),
            tint = Color(0xFFAAB3BC)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = darkBlue,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = description,
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

private fun isCriticalNotification(notification: NotificationResponse): Boolean {
    val text = "${notification.title.orEmpty()} ${notification.message.orEmpty()}".lowercase()

    return text.contains("crítica") ||
            text.contains("critica") ||
            text.contains("alerta crítica") ||
            text.contains("humedad elevada") ||
            text.contains("fuera de rango")
}
