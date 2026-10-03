package com.example.spacepulse.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacepulse.model.beans.*
import com.example.spacepulse.model.client.RetrofitClient
import com.example.spacepulse.viewmodel.ImgBBUploader
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
                val response = RetrofitClient.webService.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val token = body.token
                    val fullName = body.fullName
                    val userEmail = body.email
                    val userId = body.userId

                    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("USER_TOKEN", token)
                        putString("USER_FULL_NAME", fullName)
                        putString("USER_EMAIL", userEmail)
                        putString("USER_ID", userId)
                        apply()
                    }

                    _loginState.value = Result.success("Login exitoso")
                } else {
                    _loginState.value = Result.failure(Exception("Credenciales incorrectas"))
                }
            } catch (e: Exception) {
                _loginState.value = Result.failure(e)
            }
        }
    }

    fun registerUser(context: Context, imageUri: Uri?, email: String, pass: String, name: String, phone: String, role: String) {
        viewModelScope.launch {
            val photoUrl = if (imageUri != null) {
                ImgBBUploader.uploadPhoto(context, imageUri) ?: ""
            } else {
                ""
            }
            val registerRequest = RegisterRequest(
                email = email,
                password = pass,
                fullName = name,
                phone = phone,
                role = role,
                photo = photoUrl
            )
            try {
                val response = RetrofitClient.webService.register(registerRequest)

                if (response.isSuccessful && response.body() != null) {
                    _registerState.value = Result.success("Registro exitoso")
                } else {
                    _registerState.value = Result.failure(Exception("Error al registrar"))
                }
            } catch (e: Exception) {
                _registerState.value = Result.failure(e)
            }
        }
    }
    fun fetchProfile(token: String, userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.getUserProfile("Bearer $token", userId)
                if (response.isSuccessful) {
                    _userProfile.value = response.body()
                }
            } catch (e: Exception) {}
        }
    }
    fun addPaymentMethod(token: String, userId: String, request: AddPaymentMethodRequest) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.addPaymentMethod("Bearer $token", userId, request)
                if (response.isSuccessful) {
                    _paymentState.value = Result.success("Pago agregado")
                    fetchProfile(token, userId)
                } else {
                    _paymentState.value = Result.failure(Exception("Error al agregar pago"))
                }
            } catch (e: Exception) {
                _paymentState.value = Result.failure(e)
            }
        }
    }
    fun logout(context: Context) {
        val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }
        _loginState.value = null
        _userProfile.value = null
    }
    fun resetStates() {
        _loginState.value = null
        _registerState.value = null
        _paymentState.value = null
    }
}