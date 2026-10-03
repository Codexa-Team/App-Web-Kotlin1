package com.example.spacepulse.view

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
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
import coil.compose.AsyncImage
import com.example.spacepulse.model.beans.TaskResponse
import com.example.spacepulse.viewmodel.SpaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareasEspacioScreen(
    spaceId: Long,
    viewModel: SpaceViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
    val token = sharedPref.getString("USER_TOKEN", "") ?: ""

    val tasks by viewModel.tasksList.collectAsState()
    val isLoading by viewModel.isLoadingTasks.collectAsState()

    LaunchedEffect(spaceId) {
        viewModel.getTasksForSpace(token, spaceId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monitoreo de Tareas", fontWeight = FontWeight.Bold, color = Color(0xFF2C3E50)) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color(0xFF2C3E50))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF2C3E50))
            } else if (tasks.isEmpty()) {
                Text(
                    text = "Aún no hay tareas para este espacio.",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tasks) { task ->
                        TaskCard(task = task, viewModel = viewModel, token = token, spaceId = spaceId)
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: TaskResponse, viewModel: SpaceViewModel, token: String, spaceId: Long) {
    var showEditDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val (statusColor, statusText) = when (task.status?.uppercase()) {
        "PENDING" -> Color(0xFFFFF9C4) to "Pendiente"
        "IN_PROGRESS" -> Color(0xFFBBDEFB) to "En Proceso"
        "COMPLETED" -> Color(0xFFC8E6C9) to "Completado"
        else -> Color(0xFFEEEEEE) to (task.status ?: "Desconocido")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID: ${task.id}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Box(
                    modifier = Modifier
                        .background(statusColor, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        color = Color.DarkGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (!task.photoUrl.isNullOrBlank()) {
                AsyncImage(
                    model = task.photoUrl,
                    contentDescription = "Imagen de la tarea",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = task.title ?: "Sin título",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF2C3E50)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = task.description ?: "Sin descripción",
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.Gray)
                }
                IconButton(onClick = {
                    viewModel.deleteModelTask(token, task.id, spaceId)
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red.copy(alpha = 0.6f))
                }
            }
        }
    }

    if (showEditDialog) {
        var editTitle by remember { mutableStateOf(task.title ?: "") }
        var editDescription by remember { mutableStateOf(task.description ?: "") }
        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
        var isUploading by remember { mutableStateOf(false) }

        val galleryLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            selectedImageUri = uri
        }

        AlertDialog(
            onDismissRequest = { if (!isUploading) showEditDialog = false },
            title = { Text("Editar Solicitud", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUploading
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = editDescription,
                        onValueChange = { editDescription = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        enabled = !isUploading
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedImageUri != null) Color(0xFF4CAF50) else Color(0xFF2C3E50)
                        ),
                        enabled = !isUploading
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (selectedImageUri != null) "Foto cambiada ✓" else "Cambiar foto")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isUploading = true
                        viewModel.updateModelTask(context, selectedImageUri, token, task.id, editTitle, editDescription, spaceId)
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C3E50)),
                    enabled = !isUploading
                ) {
                    if (isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Guardar Cambios")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }, enabled = !isUploading) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }
}