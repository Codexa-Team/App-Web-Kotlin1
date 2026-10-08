package com.example.spacepulse.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spacepulse.model.beans.*
import com.example.spacepulse.model.client.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream

class SpaceViewModel : ViewModel() {

    // --- RENTICAR REAL STATE FLOWS ---
    private val _vehicles = MutableStateFlow<List<VehicleResource>>(emptyList())
    val vehicles: StateFlow<List<VehicleResource>> = _vehicles.asStateFlow()

    private val _myVehicles = MutableStateFlow<List<VehicleResource>>(emptyList())
    val myVehicles: StateFlow<List<VehicleResource>> = _myVehicles.asStateFlow()

    private val _selectedVehicle = MutableStateFlow<VehicleResource?>(null)
    val selectedVehicle: StateFlow<VehicleResource?> = _selectedVehicle.asStateFlow()

    private val _myBookings = MutableStateFlow<List<BookingResource>>(emptyList())
    val myBookings: StateFlow<List<BookingResource>> = _myBookings.asStateFlow()

    private val _myRequests = MutableStateFlow<List<BookingResource>>(emptyList())
    val myRequests: StateFlow<List<BookingResource>> = _myRequests.asStateFlow()

    private val _reviews = MutableStateFlow<List<ReviewResource>>(emptyList())
    val reviews: StateFlow<List<ReviewResource>> = _reviews.asStateFlow()

    private val _createVehicleState = MutableStateFlow<Result<String>?>(null)
    val createVehicleState: StateFlow<Result<String>?> = _createVehicleState.asStateFlow()

    private val _createBookingState = MutableStateFlow<Result<String>?>(null)
    val createBookingState: StateFlow<Result<String>?> = _createBookingState.asStateFlow()

    private val _bookingActionState = MutableStateFlow<Result<String>?>(null)
    val bookingActionState: StateFlow<Result<String>?> = _bookingActionState.asStateFlow()

    private val _createReviewState = MutableStateFlow<Result<String>?>(null)
    val createReviewState: StateFlow<Result<String>?> = _createReviewState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // --- COMPATIBILIDAD CON VISTAS PREVIAS ---
    private val _spaces = MutableStateFlow<List<SpaceResponse>>(emptyList())
    val spaces: StateFlow<List<SpaceResponse>> = _spaces.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationResponse>>(emptyList())
    val notifications: StateFlow<List<NotificationResponse>> = _notifications.asStateFlow()

    private val _iotDevices = MutableStateFlow<List<IoTDeviceResponse>>(emptyList())
    val iotDevices: StateFlow<List<IoTDeviceResponse>> = _iotDevices.asStateFlow()

    private val _myIoTDevices = MutableStateFlow<List<IoTDeviceResponse>>(emptyList())
    val myIoTDevices: StateFlow<List<IoTDeviceResponse>> = _myIoTDevices.asStateFlow()

    private val _createSpaceState = MutableStateFlow<Result<String>?>(null)
    val createSpaceState: StateFlow<Result<String>?> = _createSpaceState.asStateFlow()

    private val _deleteSpaceState = MutableStateFlow<Result<String>?>(null)
    val deleteSpaceState: StateFlow<Result<String>?> = _deleteSpaceState.asStateFlow()

    private val _addIoTDeviceState = MutableStateFlow<Result<String>?>(null)
    val addIoTDeviceState: StateFlow<Result<String>?> = _addIoTDeviceState.asStateFlow()

    private val _updateIoTDeviceState = MutableStateFlow<Result<String>?>(null)
    val updateIoTDeviceState: StateFlow<Result<String>?> = _updateIoTDeviceState.asStateFlow()

    private val _deleteIoTDeviceState = MutableStateFlow<Result<String>?>(null)
    val deleteIoTDeviceState: StateFlow<Result<String>?> = _deleteIoTDeviceState.asStateFlow()

    private val _tasksList = MutableStateFlow<List<TaskResponse>>(emptyList())
    val tasksList: StateFlow<List<TaskResponse>> = _tasksList.asStateFlow()

    private val _isLoadingTasks = MutableStateFlow(false)
    val isLoadingTasks: StateFlow<Boolean> = _isLoadingTasks.asStateFlow()

    init {
        fetchVehicles()
    }

    // ==========================================
    // VEHÍCULOS
    // ==========================================
    fun fetchVehicles() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.webService.getVehicles()
                if (response.isSuccessful && response.body() != null) {
                    val list = response.body()!!
                    _vehicles.value = list

                    // Mapeo hacia el modelo SpaceResponse para no romper vistas existentes
                    _spaces.value = list.map { v ->
                        SpaceResponse(
                            id = v.id,
                            title = "${v.brand} ${v.model} (${v.year})",
                            description = "Precio por día: $${v.pricePerDay}. Estado: ${v.status ?: "Disponible"}",
                            location = "Año: ${v.year}",
                            homeownerId = v.ownerId?.toString(),
                            spaceType = v.brand,
                            dimensionsSquareMeters = v.year.toDouble(),
                            estimatedBudget = v.pricePerDay,
                            currency = "USD",
                            images = if (!v.imageUrl.isNullOrBlank()) listOf(RetrofitClient.resolveImageUrl(v.imageUrl) ?: v.imageUrl) else emptyList(),
                            status = v.status ?: "available",
                            hasIot = true
                        )
                    }
                }
            } catch (e: Exception) {
                // Silencioso o log
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchVehicleById(vehicleId: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.getVehicleById(vehicleId)
                if (response.isSuccessful) {
                    _selectedVehicle.value = response.body()
                }
            } catch (e: Exception) { }
        }
    }

    fun fetchMyListings() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.getMyVehicles()
                if (response.isSuccessful) {
                    _myVehicles.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) { }
        }
    }

    fun createVehicle(
        context: Context,
        brand: String,
        model: String,
        year: Int,
        pricePerDay: Double,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val resourceJson = Gson().toJson(
                    CreateVehicleResource(
                        brand = brand.trim(),
                        model = model.trim(),
                        year = year,
                        pricePerDay = pricePerDay
                    )
                )
                val resourcePart = resourceJson.toRequestBody("application/json".toMediaTypeOrNull())

                val imageBytes: ByteArray = if (imageUri != null) {
                    context.contentResolver.openInputStream(imageUri)?.use { it.readBytes() }
                        ?: getFallbackImageBytes(context)
                } else {
                    getFallbackImageBytes(context)
                }

                val imageRequestBody = imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("image", "vehicle.jpg", imageRequestBody)

                val response = RetrofitClient.webService.createVehicle(resourcePart, imagePart)
                if (response.isSuccessful) {
                    _createVehicleState.value = Result.success("Vehículo publicado exitosamente")
                    _createSpaceState.value = Result.success("Vehículo publicado")
                    fetchVehicles()
                } else {
                    _createVehicleState.value = Result.failure(Exception("Error al publicar vehículo: ${response.code()}"))
                    _createSpaceState.value = Result.failure(Exception("Error al publicar"))
                }
            } catch (e: Exception) {
                _createVehicleState.value = Result.failure(e)
                _createSpaceState.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getFallbackImageBytes(context: Context): ByteArray {
        return try {
            val resId = com.example.spacepulse.R.drawable.renticar2
            val inputStream: InputStream = context.resources.openRawResource(resId)
            inputStream.readBytes()
        } catch (e: Exception) {
            ByteArray(0)
        }
    }

    fun deleteVehicle(vehicleId: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.deleteVehicle(vehicleId)
                if (response.isSuccessful) {
                    _deleteSpaceState.value = Result.success("Vehículo eliminado")
                    fetchVehicles()
                    fetchMyListings()
                }
            } catch (e: Exception) {
                _deleteSpaceState.value = Result.failure(e)
            }
        }
    }

    fun updateVehicle(
        vehicleId: Long,
        brand: String,
        model: String,
        year: Int,
        pricePerDay: Double
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val resourceJson = Gson().toJson(
                    CreateVehicleResource(
                        brand = brand.trim(),
                        model = model.trim(),
                        year = year,
                        pricePerDay = pricePerDay
                    )
                )
                val resourcePart = resourceJson.toRequestBody("application/json".toMediaTypeOrNull())
                val response = RetrofitClient.webService.updateVehicle(vehicleId, resourcePart, null)
                if (response.isSuccessful) {
                    fetchMyListings()
                    fetchVehicles()
                }
            } catch (e: Exception) {
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ==========================================
    // RESERVAS (BOOKINGS)
    // ==========================================
    fun fetchMyBookings() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.getMyBookings()
                if (response.isSuccessful) {
                    _myBookings.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) { }
        }
    }

    fun fetchMyRequests() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.getMyBookingRequests()
                if (response.isSuccessful) {
                    _myRequests.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) { }
        }
    }

    fun createBooking(vehicleId: Long, startDate: String, endDate: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = CreateBookingResource(
                    vehicleId = vehicleId,
                    startDate = startDate,
                    endDate = endDate
                )
                val response = RetrofitClient.webService.createBooking(request)
                if (response.isSuccessful) {
                    _createBookingState.value = Result.success("Reserva creada con éxito")
                    fetchMyBookings()
                } else {
                    _createBookingState.value = Result.failure(Exception("No se pudo crear la reserva (${response.code()})"))
                }
            } catch (e: Exception) {
                _createBookingState.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun confirmBooking(bookingId: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.confirmBooking(bookingId)
                if (response.isSuccessful) {
                    _bookingActionState.value = Result.success("Reserva confirmada")
                    fetchMyRequests()
                }
            } catch (e: Exception) {
                _bookingActionState.value = Result.failure(e)
            }
        }
    }

    fun rejectBooking(bookingId: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.rejectBooking(bookingId)
                if (response.isSuccessful) {
                    _bookingActionState.value = Result.success("Reserva rechazada")
                    fetchMyRequests()
                }
            } catch (e: Exception) {
                _bookingActionState.value = Result.failure(e)
            }
        }
    }

    fun cancelBooking(bookingId: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.cancelBooking(bookingId)
                if (response.isSuccessful) {
                    _bookingActionState.value = Result.success("Reserva cancelada")
                    fetchMyBookings()
                }
            } catch (e: Exception) {
                _bookingActionState.value = Result.failure(e)
            }
        }
    }


    // ==========================================
    // RESEÑAS (REVIEWS)
    // ==========================================
    fun fetchVehicleReviews(vehicleId: Long) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.webService.getVehicleReviews(vehicleId)
                if (response.isSuccessful) {
                    _reviews.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) { }
        }
    }

    fun createReview(vehicleId: Long, rating: Int, comment: String) {
        viewModelScope.launch {
            try {
                val request = CreateReviewResource(vehicleId, rating, comment)
                val response = RetrofitClient.webService.createReview(request)
                if (response.isSuccessful) {
                    _createReviewState.value = Result.success("Reseña publicada")
                    fetchVehicleReviews(vehicleId)
                }
            } catch (e: Exception) {
                _createReviewState.value = Result.failure(e)
            }
        }
    }

    // ==========================================
    // COMPATIBILIDAD CON VISTAS LEGADAS
    // ==========================================
    fun fetchSpaces(token: String, userId: String) {
        fetchVehicles()
    }

    fun fetchNotifications(token: String) {
        _notifications.value = emptyList()
    }

    fun fetchIoTDevices(token: String, spaceId: Long) { }

    fun fetchDeviceReading(token: String, deviceId: Long) { }
    fun fetchMyIoTDevices(token: String) { }

    fun addIoTDevice(token: String, request: CreateIoTDeviceRequest) {
        _addIoTDeviceState.value = Result.success("Dispositivo agregado")
    }

    fun updateIoTDevice(token: String, request: UpdateIoTDeviceRequest) {
        _updateIoTDeviceState.value = Result.success("Dispositivo actualizado")
    }

    fun updateIoTDevice(token: String, deviceId: Long, spaceId: Long, request: UpdateIoTDeviceRequest) {
        _updateIoTDeviceState.value = Result.success("Dispositivo actualizado")
    }

    fun deleteIoTDevice(token: String, id: Long, spaceId: Long = 0L) {
        _deleteIoTDeviceState.value = Result.success("Dispositivo eliminado")
    }

    fun toggleIoTDevice(token: String, deviceId: Long, spaceId: Long = 0L) { }

    fun markNotificationAsRead(token: String, id: Long, onComplete: () -> Unit = {}) {
        onComplete()
    }

    fun createSpace(context: Context, request: CreateSpaceRequest, imageUri: Uri?) {
        createVehicle(
            context = context,
            brand = request.spaceType ?: request.title,
            model = request.title,
            year = 2024,
            pricePerDay = request.estimatedBudget ?: 50.0,
            imageUri = imageUri
        )
    }

    fun deleteSpace(token: String, spaceId: Long) {
        deleteVehicle(spaceId)
    }

    fun deleteSpace(token: String, userId: String, spaceId: Long) {
        deleteVehicle(spaceId)
    }

    fun enableIotForSpace(token: String, userId: String, spaceId: Long) { }

    fun requestTask(context: Context, imageUri: Uri?, token: String, request: TaskRequest, onComplete: () -> Unit = {}) {
        onComplete()
    }

    fun getTasksForSpace(token: String, spaceId: Long) {
        _tasksList.value = emptyList()
    }

    fun deleteModelTask(token: String, taskId: Long, spaceId: Long) { }

    fun updateModelTask(context: Context, imageUri: Uri?, token: String, taskId: Long, title: String, description: String, spaceId: Long) { }

    fun registerUser(context: Context, imageUri: Uri?, email: String, pass: String, name: String, phone: String) { }

    fun cancelSpaceDDD(token: String, userId: String, spaceId: Long) { }

    fun completeSpace(token: String, userId: String, spaceId: Long, onResult: (Boolean) -> Unit = {}) {
        onResult(true)
    }

    fun resetStates() {
        _createVehicleState.value = null
        _createBookingState.value = null
        _bookingActionState.value = null
        _createReviewState.value = null
        _createSpaceState.value = null
        _deleteSpaceState.value = null
        _addIoTDeviceState.value = null
        _updateIoTDeviceState.value = null
        _deleteIoTDeviceState.value = null
    }
}
