package com.example.spacepulse.view

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.MarkEmailUnread
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.spacepulse.viewmodel.AuthViewModel
import com.example.spacepulse.viewmodel.SpaceViewModel

@Composable
fun ClientHomeScreen(navController: NavController, viewModel: AuthViewModel, spaceViewModel: SpaceViewModel) {
    val context = LocalContext.current
    val darkBlue = Color(0xFF2C3E50)
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }

    val sharedPref = remember { context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE) }
    val userRole = sharedPref.getString("USER_ROLE", "ROLE_ARRENDATARIO") ?: "ROLE_ARRENDATARIO"
    val isOwner = userRole.contains("arrendador", ignoreCase = true) || userRole.contains("propietario", ignoreCase = true)

    Scaffold(
        bottomBar = {
            if (isOwner) {
                RenticarOwnerBottomNavigation(darkBlue, selectedItem) { index ->
                    selectedItem = index
                }
            } else {
                RenticarClientBottomNavigation(darkBlue, selectedItem) { index ->
                    selectedItem = index
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isOwner) {
                // Modo Propietario (Arrendador)
                when (selectedItem) {
                    0 -> MisCarrosOwnerView(
                        navController = navController,
                        spaceViewModel = spaceViewModel,
                        onNavigateToPublish = { selectedItem = 1 }
                    )
                    1 -> CrearEspacioScreen(
                        navController = navController,
                        spaceViewModel = spaceViewModel,
                        onSuccess = { selectedItem = 0 }
                    )
                    2 -> SolicitudesOwnerView(
                        navController = navController,
                        spaceViewModel = spaceViewModel
                    )
                    3 -> PerfilView(navController, viewModel)
                }
            } else {
                // Modo Cliente (Arrendatario)
                when (selectedItem) {
                    0 -> DashboardView(
                        navController = navController,
                        spaceViewModel = spaceViewModel,
                        authViewModel = viewModel,
                        onTabSelected = { selectedItem = it }
                    )
                    1 -> EspaciosView(navController, spaceViewModel)
                    2 -> BuscarView(navController, spaceViewModel)
                    3 -> PerfilView(navController, viewModel)
                }
            }
        }
    }
}

@Composable
fun RenticarClientBottomNavigation(selectedColor: Color, selectedItem: Int, onItemSelected: (Int) -> Unit) {
    val items = listOf("Catálogo", "Mis Reservas", "Buscar", "Perfil")
    val icons = listOf(
        Icons.Outlined.DirectionsCar,
        Icons.Outlined.AssignmentTurnedIn,
        Icons.Outlined.Search,
        Icons.Outlined.Person
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Gray
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item, fontSize = 10.sp) },
                selected = selectedItem == index,
                onClick = { onItemSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    selectedTextColor = selectedColor,
                    unselectedIconColor = Color.LightGray,
                    unselectedTextColor = Color.LightGray,
                    indicatorColor = Color(0xFFB3E5FC)
                )
            )
        }
    }
}

@Composable
fun RenticarOwnerBottomNavigation(selectedColor: Color, selectedItem: Int, onItemSelected: (Int) -> Unit) {
    val items = listOf("Mis Autos", "Publicar", "Solicitudes", "Perfil")
    val icons = listOf(
        Icons.Outlined.DirectionsCar,
        Icons.Outlined.AddCircleOutline,
        Icons.Outlined.MarkEmailUnread,
        Icons.Outlined.Person
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Gray
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item, fontSize = 10.sp) },
                selected = selectedItem == index,
                onClick = { onItemSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    selectedTextColor = selectedColor,
                    unselectedIconColor = Color.LightGray,
                    unselectedTextColor = Color.LightGray,
                    indicatorColor = Color(0xFFB3E5FC)
                )
            )
        }
    }
}