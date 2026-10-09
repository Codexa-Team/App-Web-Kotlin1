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

    private val _updateProfileState = MutableStateFlow<Result<String>?>(null)
    val updateProfileState: StateFlow<Result<String>?> = _updateProfileState

    private val _updatePasswordState = MutableStateFlow<Result<String>?>(null)
    val updatePasswordState: StateFlow<Result<String>?> = _updatePasswordState

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

    fun updateEmail(context: Context, newEmail: String) {
        viewModelScope.launch {
            try {
                val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
                val userIdStr = sharedPref.getString("USER_ID", "") ?: ""
                val userIdLong = userIdStr.toLongOrNull() ?: _userProfile.value?.id?.toLongOrNull()

                if (userIdLong == null) {
                    _updateProfileState.value = Result.failure(Exception("ID de usuario no encontrado"))
                    return@launch
                }

                val currentName = sharedPref.getString("USER_FULL_NAME", "")?.takeIf { it.isNotBlank() }
                    ?: _userProfile.value?.fullName
                    ?: "Usuario"

                val request = UpdateUserRequest(
                    name = currentName,
                    email = newEmail.trim()
                )

                val response = RetrofitClient.webService.updateUserProfile(userIdLong, request)
                if (response.isSuccessful && response.body() != null) {
                    val updatedUser = response.body()!!

                    // Actualizar en SharedPreferences
                    with(sharedPref.edit()) {
                        putString("USER_EMAIL", updatedUser.email)
                        apply()
                    }

                    // Actualizar en memoria
                    _userProfile.value = _userProfile.value?.copy(
                        email = updatedUser.email
                    ) ?: UserProfileResponse(
                        id = updatedUser.id.toString(),
                        fullName = updatedUser.name,
                        email = updatedUser.email,
                        phone = null,
                        role = updatedUser.roles.firstOrNull() ?: "ROLE_ARRENDATARIO",
                        photo = null,
                        paymentMethods = emptyList()
                    )

                    _updateProfileState.value = Result.success("Correo actualizado correctamente")
                } else {
                    val errorMsg = if (response.code() == 409) "El correo ya está registrado por otro usuario" else "Error al actualizar correo (${response.code()})"
                    _updateProfileState.value = Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                _updateProfileState.value = Result.failure(Exception(e.localizedMessage ?: "Error de conexión con el servidor"))
            }
        }
    }

    fun resetUpdateProfileState() {
        _updateProfileState.value = null
    }

    fun updatePassword(context: Context, currentPass: String, newPass: String) {
        viewModelScope.launch {
            try {
                val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
                val token = sharedPref.getString("USER_TOKEN", "") ?: ""
                if (RetrofitClient.authToken.isNullOrBlank() && token.isNotBlank()) {
                    RetrofitClient.authToken = token
                }

                val userIdStr = sharedPref.getString("USER_ID", "") ?: ""
                val userIdLong = userIdStr.toLongOrNull() ?: _userProfile.value?.id?.toLongOrNull()

                if (userIdLong == null) {
                    _updatePasswordState.value = Result.failure(Exception("ID de usuario no encontrado"))
                    return@launch
                }

                val request = UpdatePasswordRequest(
                    currentPassword = currentPass.trim(),
                    newPassword = newPass.trim()
                )

                val response = RetrofitClient.webService.updatePassword(userIdLong, request)
                if (response.isSuccessful) {
                    _updatePasswordState.value = Result.success("Contraseña actualizada con éxito")
                } else {
                    val errorMsg = when (response.code()) {
                        400, 401, 500 -> "La contraseña actual es incorrecta o no cumple los requisitos"
                        404 -> "Usuario no encontrado"
                        else -> "Error al cambiar contraseña (${response.code()})"
                    }
                    _updatePasswordState.value = Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                _updatePasswordState.value = Result.failure(Exception(e.localizedMessage ?: "Error al conectar con el servidor"))
            }
        }
    }

    fun resetUpdatePasswordState() {
        _updatePasswordState.value = null
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
        _updateProfileState.value = null
        _updatePasswordState.value = null
    }

    fun resetStates() {
        _loginState.value = null
        _registerState.value = null
        _paymentState.value = null
        _updateProfileState.value = null
        _updatePasswordState.value = null
    }
}