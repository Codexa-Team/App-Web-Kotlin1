package com.example.spacepulse.view

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
fun DetalleEspacioScreen(navController: NavController, spaceViewModel: SpaceViewModel, spaceId: Long) {
    val darkBlue = Color(0xFF2C3E50)
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
    val token = sharedPref.getString("USER_TOKEN", "") ?: ""
    val userId = sharedPref.getString("USER_ID", "") ?: ""

    val spaces by spaceViewModel.spaces.collectAsState()
    val space = spaces.find { it.id == spaceId }
    
    val isFinished = space?.status?.uppercase() in listOf("3", "COMPLETED", "COMPLETADO", "FINISHED", "FINALIZADO", "4", "CANCELLED", "CANCELADO")
    val statusLabel = when (space?.status?.uppercase()) {
        "0", "PUBLISHED", "PUBLICADO" -> "Publicado"
        "1", "ACCEPTED", "ACEPTADO" -> "Aceptado"
        "2", "IN_PROGRESS", "EN_PROCESO" -> "En Progreso"
        "3", "COMPLETED", "COMPLETADO", "FINISHED", "FINALIZADO" -> "Completado"
        "4", "CANCELLED", "CANCELADO" -> "Cancelado"
        else -> space?.status
    }

    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCompletedAlert by remember { mutableStateOf(false) }
    var showTasksIncompleteAlert by remember { mutableStateOf(false) }

    val deleteState by spaceViewModel.deleteSpaceState.collectAsState()

    LaunchedEffect(deleteState) {
        if (deleteState?.isSuccess == true) {
            spaceViewModel.resetStates()
            navController.popBackStack()
        }
    }

    val tasksList by spaceViewModel.tasksList.collectAsState()
    LaunchedEffect(spaceId) {
        if (token.isNotEmpty()) {
            spaceViewModel.getTasksForSpace(token, spaceId)
        }
    }

    val completadas = tasksList.count { it.status.equals("COMPLETED", ignoreCase = true) || it.status.equals("Completada", ignoreCase = true) }
    val enProceso = tasksList.count { it.status.equals("IN_PROGRESS", ignoreCase = true) || it.status.equals("En proceso", ignoreCase = true) }
    val pendientes = tasksList.count { it.status.equals("PENDING", ignoreCase = true) || it.status.equals("Pendiente", ignoreCase = true) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar espacio", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = darkBlue) },
            text = { Text("Esta acción quitará el espacio de tu\nlista", color = Color.Gray, fontSize = 16.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        spaceViewModel.deleteSpace(token, userId, spaceId)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.LightGray)
                ) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showTasksIncompleteAlert) {
        AlertDialog(
            onDismissRequest = { showTasksIncompleteAlert = false },
            title = { Text("Tareas Pendientes", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = darkBlue) },
            text = { Text("No se puede completar el espacio porque aún hay tareas pendientes o en proceso.", color = Color.Gray, fontSize = 16.sp) },
            confirmButton = {
                Button(
                    onClick = { showTasksIncompleteAlert = false },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
                ) {
                    Text("Entendido", color = Color.White)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (showCompletedAlert) {
        AlertDialog(
            onDismissRequest = { showCompletedAlert = false },
            title = { Text("Aprobación Requerida", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = darkBlue) },
            text = { Text("Para cambiar el estado a completado, este proyecto primero tiene que ser aprobado por el remodelador.", color = Color.Gray, fontSize = 16.sp) },
            confirmButton = {
                Button(
                    onClick = { showCompletedAlert = false },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
                ) {
                    Text("Entendido", color = Color.White)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de espacio", fontWeight = FontWeight.Bold, color = darkBlue, fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = darkBlue)
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Opciones", tint = Color.Gray)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            if (!isFinished) {
                                DropdownMenuItem(
                                    text = { Text("Marcar como Completado", color = darkBlue) },
                                    onClick = {
                                        showMenu = false
                                        if (enProceso == 0 && pendientes == 0) {
                                            spaceViewModel.completeSpace(token, userId, spaceId) { success ->
                                                if (success) {
                                                    navController.popBackStack()
                                                } else {
                                                    showCompletedAlert = true
                                                }
                                            }
                                        } else {
                                            showTasksIncompleteAlert = true
                                        }
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Cancelar Espacio", color = darkBlue) },
                                    onClick = {
                                        showMenu = false
                                        spaceViewModel.cancelSpaceDDD(token, userId, spaceId)
                                        navController.popBackStack()
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = { Text("Eliminar", color = Color.Red) },
                                onClick = {
                                    showMenu = false
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = space?.title ?: "Cargando...", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = darkBlue)
                        
                        if (statusLabel != null) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = when (space?.status?.uppercase()) {
                                    "3", "COMPLETED", "COMPLETADO", "FINISHED", "FINALIZADO" -> Color(0xFFE8F5E9)
                                    "4", "CANCELLED", "CANCELADO" -> Color(0xFFFFEBEE)
                                    "2", "IN_PROGRESS", "EN_PROCESO" -> Color(0xFFE3F2FD)
                                    else -> Color(0xFFF5F5F5)
                                }
                            ) {
                                Text(
                                    text = statusLabel,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (space?.status?.uppercase()) {
                                        "3", "COMPLETED", "COMPLETADO", "FINISHED", "FINALIZADO" -> Color(0xFF2E7D32)
                                        "4", "CANCELLED", "CANCELADO" -> Color(0xFFC62828)
                                        "2", "IN_PROGRESS", "EN_PROCESO" -> Color(0xFF1976D2)
                                        else -> Color.DarkGray
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = space?.location ?: "", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Estado: ${space?.status ?: ""}", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Presupuesto: ${space?.currency ?: ""} ${space?.estimatedBudget ?: ""}", color = Color.Gray, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Resumen de tareas", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {}, shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, Color(0xFF27AE60)), contentPadding = PaddingValues(horizontal = 12.dp)) {
                    Text("$completadas completadas", color = Color.DarkGray, fontSize = 12.sp)
                }
                OutlinedButton(onClick = {}, shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, darkBlue), contentPadding = PaddingValues(horizontal = 12.dp)) {
                    Text("$enProceso en proceso", color = Color.DarkGray, fontSize = 12.sp)
                }
                OutlinedButton(onClick = {}, shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, Color(0xFFD68910)), contentPadding = PaddingValues(horizontal = 12.dp)) {
                    Text("$pendientes pendiente${if (pendientes != 1) "s" else ""}", color = Color.DarkGray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Opciones", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = { navController.navigate("monitoreoEspacio/$spaceId") },
                    modifier = Modifier.weight(1f).height(80.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    enabled = !isFinished
                ) {
                    Text("Monitoreo", color = if (isFinished) Color.Gray else darkBlue, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    navController.navigate("solicitarTarea/${spaceId}")
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = darkBlue),
                enabled = !isFinished
            ) {
                Text("Solicitar Nueva Tarea", fontSize = 16.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    navController.navigate("tareasEspacio/$spaceId")
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C3E50))
            ) {
                Text("Ver Lista de Tareas", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}