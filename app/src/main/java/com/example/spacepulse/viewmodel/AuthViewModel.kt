package com.example.spacepulse.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacepulse.model.beans.*
import com.example.spacepulse.model.client.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<Result<String>?>(null)
    val loginState: StateFlow<Result<String>?> = _loginState

    private val _registerState = MutableStateFlow<Result<String>?>(null)
    val registerState: StateFlow<Result<String>?> = _registerState

    private val _userProfile = MutableStateFlow<UserProfileResponse?>(null)
    val userProfile: StateFlow<UserProfileResponse?> = _userProfile

    private val _paymentState = MutableStateFlow<Result<String>?>(null)
    val paymentState: StateFlow<Result<String>?> = _paymentState

    fun login(email: String, password: String, context: Context) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.signIn(SignInRequest(email = email.trim(), password = password))
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val token = body.token
                    val fullName = body.name
                    val userEmail = body.email
                    val userId = body.id.toString()
                    val userRole = body.roles.firstOrNull() ?: "ROLE_ARRENDATARIO"

                    // Configurar el token activo en RetrofitClient
                    RetrofitClient.authToken = token

                    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("USER_TOKEN", token)
                        putString("USER_FULL_NAME", fullName)
                        putString("USER_EMAIL", userEmail)
                        putString("USER_ID", userId)
                        putString("USER_ROLE", userRole)
                        apply()
                    }

                    _loginState.value = Result.success("Login exitoso")
                } else {
                    _loginState.value = Result.failure(Exception("Correo o contraseña incorrectos"))
                }
            } catch (e: Exception) {
                _loginState.value = Result.failure(Exception(e.localizedMessage ?: "Error de conexión con el backend"))
            }
        }
    }

    fun registerUser(
        context: Context,
        imageUri: Uri?,
        email: String,
        pass: String,
        name: String,
        phone: String,
        role: String
    ) {
        viewModelScope.launch {
            // Mapear rol a lo que espera el Backend ("arrendador" o "arrendatario")
            val cleanRole = when {
                role.contains("arrendador", ignoreCase = true) || role.contains("propietario", ignoreCase = true) -> "arrendador"
                else -> "arrendatario"
            }

            val signUpRequest = SignUpRequest(
                name = name.trim(),
                email = email.trim(),
                password = pass,
                role = cleanRole
            )

            try {
                val response = RetrofitClient.webService.signUp(signUpRequest)
                if (response.isSuccessful && response.body() != null) {
                    _registerState.value = Result.success("Registro exitoso")
                } else {
                    val errorMsg = if (response.code() == 409) "El correo ya se encuentra registrado" else "Error al registrar usuario"
                    _registerState.value = Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                _registerState.value = Result.failure(Exception(e.localizedMessage ?: "Error al conectar con el servidor"))
            }
        }
    }

    fun fetchProfile(token: String, userId: String) {
        viewModelScope.launch {
            try {
                if (RetrofitClient.authToken.isNullOrBlank()) {
                    RetrofitClient.authToken = token
                }
                val uIdLong = userId.toLongOrNull() ?: return@launch
                val response = RetrofitClient.webService.getUserProfileById(uIdLong)
                if (response.isSuccessful && response.body() != null) {
                    val u = response.body()!!
                    _userProfile.value = UserProfileResponse(
                        id = u.id.toString(),
                        fullName = u.name,
                        email = u.email,
                        phone = null,
                        role = u.roles.firstOrNull() ?: "ROLE_ARRENDATARIO",
                        photo = null,
                        paymentMethods = emptyList()
                    )
                }
            } catch (e: Exception) {
                // Silencioso o log
            }
        }
    }

    fun addPaymentMethod(token: String, userId: String, request: AddPaymentMethodRequest) {
        viewModelScope.launch {
            _paymentState.value = Result.success("Método guardado")
        }
    }

    fun logout(context: Context) {
        val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }
        RetrofitClient.authToken = null
        _loginState.value = null
        _userProfile.value = null
    }

    fun resetStates() {
        _loginState.value = null
        _registerState.value = null
        _paymentState.value = null
    }
}