package com.example.spacepulse.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacepulse.model.beans.CreateIoTDeviceRequest
import com.example.spacepulse.model.beans.CreateSpaceRequest
import com.example.spacepulse.model.beans.IoTDeviceResponse
import com.example.spacepulse.model.beans.NotificationResponse
import com.example.spacepulse.model.beans.RegisterRequest
import com.example.spacepulse.model.beans.SpaceResponse
import com.example.spacepulse.model.beans.TaskRequest
import com.example.spacepulse.model.beans.TaskResponse
import com.example.spacepulse.model.beans.UpdateIoTDeviceRequest
import com.example.spacepulse.model.client.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SpaceViewModel : ViewModel() {

    private val _tasksList = MutableStateFlow<List<TaskResponse>>(emptyList())
    val tasksList: StateFlow<List<TaskResponse>> = _tasksList.asStateFlow()
    private val _isLoadingTasks = MutableStateFlow(false)
    val isLoadingTasks: StateFlow<Boolean> = _isLoadingTasks.asStateFlow()
    private val _spaces = MutableStateFlow<List<SpaceResponse>>(emptyList())
    val spaces: StateFlow<List<SpaceResponse>> = _spaces
    private val _iotDevices = MutableStateFlow<List<IoTDeviceResponse>>(emptyList())
    val iotDevices: StateFlow<List<IoTDeviceResponse>> = _iotDevices
    private val _myIoTDevices = MutableStateFlow<List<IoTDeviceResponse>>(emptyList())
    val myIoTDevices: StateFlow<List<IoTDeviceResponse>> = _myIoTDevices
    private val _notifications = MutableStateFlow<List<NotificationResponse>>(emptyList())
    val notifications: StateFlow<List<NotificationResponse>> = _notifications
    private val _createSpaceState = MutableStateFlow<Result<String>?>(null)
    val createSpaceState: StateFlow<Result<String>?> = _createSpaceState
    private val _deleteSpaceState = MutableStateFlow<Result<String>?>(null)
    val deleteSpaceState: StateFlow<Result<String>?> = _deleteSpaceState
    private val _addIoTDeviceState = MutableStateFlow<Result<String>?>(null)
    val addIoTDeviceState: StateFlow<Result<String>?> = _addIoTDeviceState
    private val _updateIoTDeviceState = MutableStateFlow<Result<String>?>(null)
    val updateIoTDeviceState: StateFlow<Result<String>?> = _updateIoTDeviceState
    private val _deleteIoTDeviceState = MutableStateFlow<Result<String>?>(null)
    val deleteIoTDeviceState: StateFlow<Result<String>?> = _deleteIoTDeviceState
    fun fetchSpaces(token: String, userId: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.getMySpaces("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    _spaces.value = response.body()!!
                }
            } catch (e: Exception) {
            }
        }
    }
    fun fetchIoTDevices(token: String, spaceId: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.getIoTDevicesBySpace("Bearer $token", spaceId)
                if (response.isSuccessful) {
                    _iotDevices.value = response.body() ?: emptyList()
                } else {
                    _iotDevices.value = emptyList()
                }
            } catch (e: Exception) {
                _iotDevices.value = emptyList()
            }
        }
    }

    fun fetchDeviceReading(token: String, deviceId: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.getReadingByDeviceId("Bearer $token", deviceId)
                if (response.isSuccessful && response.body() != null) {
                    val updatedDevice = response.body()!!
                    _iotDevices.value = _iotDevices.value.map { if (it.id == deviceId) updatedDevice else it }
                    _myIoTDevices.value = _myIoTDevices.value.map { if (it.id == deviceId) updatedDevice else it }
                }
            } catch (e: Exception) {}
        }
    }
    fun fetchMyIoTDevices(token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.getMyIoTDevices("Bearer $token")
                if (response.isSuccessful) {
                    _myIoTDevices.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {}
        }
    }
    fun addIoTDevice(token: String, request: CreateIoTDeviceRequest) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.addIoTDevice("Bearer $token", request)
                if (response.isSuccessful) {
                    _addIoTDeviceState.value = Result.success("Dispositivo agregado")
                    fetchIoTDevices(token, request.spaceId)
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("IoTDevice", "Error body: $errorBody")
                    _addIoTDeviceState.value = Result.failure(Exception("Error ${response.code()}: $errorBody"))
                }
            } catch (e: Exception) {
                _addIoTDeviceState.value = Result.failure(e)
            }
        }
    }
    fun updateIoTDevice(token: String, deviceId: Long, spaceId: Long, request: UpdateIoTDeviceRequest) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.updateIoTDevice("Bearer $token", deviceId, request)
                if (response.isSuccessful) {
                    _updateIoTDeviceState.value = Result.success("Dispositivo actualizado")
                    fetchIoTDevices(token, spaceId)
                } else {
                    _updateIoTDeviceState.value = Result.failure(Exception("Error al actualizar dispositivo"))
                }
            } catch (e: Exception) {
                _updateIoTDeviceState.value = Result.failure(e)
            }
        }
    }
    fun toggleIoTDevice(token: String, deviceId: Long, spaceId: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.toggleIoTDevice("Bearer $token", deviceId)
                if (response.isSuccessful) {
                    fetchIoTDevices(token, spaceId)
                }
            } catch (e: Exception) {}
        }
    }
    fun deleteIoTDevice(token: String, deviceId: Long, spaceId: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.deleteIoTDevice("Bearer $token", deviceId)
                if (response.isSuccessful) {
                    _deleteIoTDeviceState.value = Result.success("Dispositivo eliminado")
                    fetchIoTDevices(token, spaceId)
                } else {
                    _deleteIoTDeviceState.value = Result.failure(Exception("Error al eliminar dispositivo"))
                }
            } catch (e: Exception) {
                _deleteIoTDeviceState.value = Result.failure(e)
            }
        }
    }
    fun fetchNotifications(token: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.getUserNotifications("Bearer $token")
                if (response.isSuccessful) {
                    _notifications.value = response.body() ?: emptyList()
                } else {
                    _notifications.value = emptyList()
                }
            } catch (e: Exception) {
                _notifications.value = emptyList()
            }
        }
    }
    fun markNotificationAsRead(token: String, notificationId: Long, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.markNotificationAsRead(
                    "Bearer $token",
                    notificationId
                )

                if (response.isSuccessful) {
                    _notifications.value = _notifications.value.map { notification ->
                        if (notification.id == notificationId) {
                            notification.copy(isRead = true)
                        } else {
                            notification
                        }
                    }
                } else {
                    android.util.Log.e(
                        "NOTIFICATIONS",
                        "Error al marcar como leída: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e(
                    "NOTIFICATIONS",
                    "Fallo al marcar como leída: ${e.message}"
                )
            } finally {
                onComplete()
            }
        }
    }
    fun createSpace(context: Context, imageUri: Uri?, token: String, userId: String, request: CreateSpaceRequest) {
        viewModelScope.launch {
            try {
                val photoUrl = if (imageUri != null) {
                    ImgBBUploader.uploadPhoto(context, imageUri) ?: ""
                } else {
                    ""
                }

                val imagesList = if (photoUrl.isNotEmpty()) listOf(photoUrl) else emptyList()

                val finalRequest = request.copy(images = imagesList)

                val response = RetrofitClient.webService.createSpace("Bearer $token", finalRequest)

                if (response.isSuccessful) {
                    _createSpaceState.value = Result.success("Espacio creado")
                    fetchSpaces(token, userId)
                } else {
                    _createSpaceState.value = Result.failure(Exception("Error al crear: ${response.code()}"))
                }
            } catch (e: Exception) {
                _createSpaceState.value = Result.failure(e)
            }
        }
    }
    fun deleteSpace(token: String, userId: String, id: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.deleteSpace("Bearer $token", id)
                if (response.isSuccessful) {
                    _deleteSpaceState.value = Result.success("Eliminado")
                    fetchSpaces(token, userId)
                } else {
                    _deleteSpaceState.value = Result.failure(Exception("Error al eliminar"))
                }
            } catch (e: Exception) {
                _deleteSpaceState.value = Result.failure(e)
            }
        }
    }
    fun enableIotForSpace(token: String, userId: String, spaceId: Long) {
        viewModelScope.launch {
            try {
                val space = _spaces.value.find { it.id == spaceId }
                if (space != null) {
                    val request = com.example.spacepulse.model.beans.UpdateSpaceRequest(
                        title = space.title,
                        description = space.description,
                        location = space.location,
                        dimensionsSquareMeters = space.dimensionsSquareMeters ?: 0.0,
                        estimatedBudget = space.estimatedBudget ?: 0.0,
                        hasIot = true,
                        images = emptyList(),
                        status = space.status
                    )
                    val response = RetrofitClient.webService.updateSpace("Bearer $token", spaceId, request)
                    if (response.isSuccessful) {
                        fetchSpaces(token, userId)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("SpaceViewModel", "Error enabling IoT: ${e.message}")
            }
        }
    }
    fun resetStates() {
        _createSpaceState.value = null
        _deleteSpaceState.value = null
        _addIoTDeviceState.value = null
        _updateIoTDeviceState.value = null
        _deleteIoTDeviceState.value = null
    }
    fun requestTask(context: Context, imageUri: Uri?, token: String, request: TaskRequest, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                val photoUrl = if (imageUri != null) {
                    ImgBBUploader.uploadPhoto(context, imageUri) ?: ""
                } else {
                    ""
                }

                val finalRequest = request.copy(photoUrl = photoUrl)
                val response = RetrofitClient.webService.requestTask("Bearer $token", finalRequest)
                if (response.isSuccessful) {
                    onComplete()
                } else {
                    onComplete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete()
            }
        }
    }
    fun getTasksForSpace(token: String, spaceId: Long) {
        viewModelScope.launch {
            _isLoadingTasks.value = true
            try {
                val response = RetrofitClient.webService.getTasksBySpaceId("Bearer $token", spaceId)
                if (response.isSuccessful) {
                    _tasksList.value = response.body() ?: emptyList()
                } else {
                    android.util.Log.e("TASKS", "Error del servidor: ${response.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("TASKS", "Fallo de conexión: ${e.message}")
            } finally {
                _isLoadingTasks.value = false
            }
        }
    }
    fun deleteModelTask(token: String, taskId: Long, spaceId: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.deleteWorkItem("Bearer $token", taskId)
                if (response.isSuccessful) {
                    getTasksForSpace(token, spaceId)
                } else {
                    android.util.Log.e("TASKS", "Error al eliminar: ${response.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("TASKS", "Fallo al eliminar: ${e.message}")
            }
        }
    }
    fun updateModelTask(context: Context, imageUri: Uri?, token: String, taskId: Long, title: String, description: String, spaceId: Long) {
        viewModelScope.launch {
            try {
                val photoUrl = if (imageUri != null) {
                    ImgBBUploader.uploadPhoto(context, imageUri) ?: ""
                } else {
                    ""
                }
                val request = com.example.spacepulse.model.beans.UpdateTaskContentRequest(
                    title,
                    description,
                    photoUrl
                )
                val response =
                    RetrofitClient.webService.updateTaskContent("Bearer $token", taskId, request)
                if (response.isSuccessful) {
                    getTasksForSpace(token, spaceId)
                } else {
                    android.util.Log.e("TASKS", "Error al editar: ${response.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("TASKS", "Fallo al editar: ${e.message}")
            }
        }
    }
    fun registerUser(context: Context, imageUri: Uri?, email: String, pass: String, name: String, phone: String) {
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
                role = "Homeowner",
                photo = photoUrl
            )
            try {
                val response = RetrofitClient.webService.register(registerRequest)
            } catch (e: Exception) {
            }
        }
    }
    fun cancelSpaceDDD(token: String, userId: String, spaceId: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.cancelSpace("Bearer $token", spaceId)
                if (response.isSuccessful) {
                    fetchSpaces(token, userId)
                    android.util.Log.d("SpaceViewModel", "Proyecto cancelado con éxito en el backend")
                } else {
                    android.util.Log.e("SpaceViewModel", "Error al cancelar: ${response.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("SpaceViewModel", "Fallo al cancelar: ${e.message}")
            }
        }
    }

    fun completeSpace(token: String, userId: String, spaceId: Long, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.completeSpace("Bearer $token", spaceId)
                if (response.isSuccessful) {
                    fetchSpaces(token, userId)
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
}



