package com.example.spacepulse.view.nav

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spacepulse.view.*
import com.example.spacepulse.viewmodel.AuthViewModel
import com.example.spacepulse.viewmodel.SpaceViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val spaceViewModel: SpaceViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController, viewModel = authViewModel)
        }
        composable("register") {
            RegisterScreen(navController = navController, viewModel = authViewModel)
        }
        composable("clientHome") {
            ClientHomeScreen(navController = navController, viewModel = authViewModel, spaceViewModel = spaceViewModel)
        }
        composable("editarPerfil") {
            EditarPerfilScreen(navController = navController)
        }
        composable("metodosPago") {
            MetodosPagoScreen(navController = navController, viewModel = authViewModel)
        }
        composable("agregarPago") {
            AgregarPagoScreen(navController = navController, viewModel = authViewModel)
        }
        composable("configuracion") {
            ConfiguracionScreen(navController = navController)
        }
        composable("crearEspacio") {
            CrearEspacioScreen(navController = navController, spaceViewModel = spaceViewModel)
        }
        composable("detalleEspacio/{spaceId}") { backStackEntry ->
            val spaceId = backStackEntry.arguments?.getString("spaceId")?.toLongOrNull() ?: 0L
            DetalleEspacioScreen(navController = navController, spaceViewModel = spaceViewModel, spaceId = spaceId)
        }
        composable("monitoreoEspacio/{spaceId}") { backStackEntry ->
            val spaceId = backStackEntry.arguments?.getString("spaceId")?.toLongOrNull() ?: 0L
            MonitoreoEspacioScreen(navController = navController, spaceViewModel = spaceViewModel, spaceId = spaceId)
        }
        composable("detalleAlerta/{notificationId}") { backStackEntry ->
            val notificationId = backStackEntry.arguments?.getString("notificationId")?.toLongOrNull() ?: 0L
            DetalleAlertaScreen(navController = navController, spaceViewModel = spaceViewModel, notificationId = notificationId)
        }
        composable("agregarIoTDevice/{spaceId}") { backStackEntry ->
            val spaceId = backStackEntry.arguments?.getString("spaceId")?.toLongOrNull() ?: 0L
            AgregarIoTDeviceScreen(navController = navController, spaceViewModel = spaceViewModel, spaceId = spaceId)
        }
        composable("detalleIoTDevice/{deviceId}") { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getString("deviceId")?.toLongOrNull() ?: 0L
            DetalleIoTDeviceScreen(navController = navController, spaceViewModel = spaceViewModel, deviceId = deviceId)
        }
        composable("solicitarTarea/{spaceId}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("spaceId")?.toLongOrNull() ?: 0L
            com.example.spacepulse.view.SolicitarTareaScreen(
                navController = navController,
                spaceViewModel = spaceViewModel,
                spaceId = id
            )
        }

        composable("tareasEspacio/{spaceId}") { backStackEntry ->
            val spaceId = backStackEntry.arguments?.getString("spaceId")?.toLongOrNull() ?: 0L

            TareasEspacioScreen(
                spaceId = spaceId,
                viewModel = spaceViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}