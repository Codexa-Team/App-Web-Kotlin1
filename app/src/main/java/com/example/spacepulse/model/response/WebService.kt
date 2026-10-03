package com.example.spacepulse.model.response

import com.example.spacepulse.model.beans.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface WebService {
    @POST("api/users/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/users/register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>
    @GET("api/users/{userId}")
    suspend fun getUserProfile(@Header("Authorization") token: String, @Path("userId") userId: String): Response<UserProfileResponse>

    @POST("api/users/{userId}/payment-methods")
    suspend fun addPaymentMethod(@Header("Authorization") token: String, @Path("userId") userId: String, @Body request: AddPaymentMethodRequest): Response<UserProfileResponse>

    @GET("api/v1/space")
    suspend fun getSpaces(@Header("Authorization") token: String): Response<List<SpaceResponse>>

    @GET("api/v1/space/my-spaces")
    suspend fun getMySpaces(@Header("Authorization") token: String): Response<List<SpaceResponse>>

    @POST("api/v1/space")
    suspend fun createSpace(@Header("Authorization") token: String, @Body request: CreateSpaceRequest): Response<SpaceResponse>

    @DELETE("api/v1/space/{id}")
    suspend fun deleteSpace(@Header("Authorization") token: String, @Path("id") id: Long): Response<Unit>

    @PUT("api/v1/space/{id}")
    suspend fun updateSpace(@Header("Authorization") token: String, @Path("id") id: Long, @Body request: UpdateSpaceRequest): Response<SpaceResponse>

    @GET("api/v1/monitoring/readings/space/{spaceId}")
    suspend fun getIoTDevicesBySpace(@Header("Authorization") token: String, @Path("spaceId") spaceId: Long): Response<List<IoTDeviceResponse>>

    @GET("api/v1/monitoring/readings/device/{deviceId}")
    suspend fun getReadingByDeviceId(@Header("Authorization") token: String, @Path("deviceId") deviceId: Long): Response<IoTDeviceResponse>

    @GET("api/v1/monitoring/io-t-devices/space/{spaceId}")
    suspend fun getIoTDevicesBySpaceId(@Header("Authorization") token: String, @Path("spaceId") spaceId: Long): Response<List<IoTDeviceResponse>>
    @POST("api/v1/monitoring/io-t-devices")
    suspend fun addIoTDevice(@Header("Authorization") token: String, @Body request: CreateIoTDeviceRequest): Response<IoTDeviceResponse>

    @PUT("api/v1/monitoring/io-t-devices/{deviceId}")
    suspend fun updateIoTDevice(@Header("Authorization") token: String, @Path("deviceId") deviceId: Long, @Body request: UpdateIoTDeviceRequest): Response<IoTDeviceResponse>

    @PUT("api/v1/monitoring/io-t-devices/{deviceId}/toggle")
    suspend fun toggleIoTDevice(@Header("Authorization") token: String, @Path("deviceId") deviceId: Long): Response<Unit>

    @GET("api/v1/monitoring/io-t-devices/my-devices")
    suspend fun getMyIoTDevices(@Header("Authorization") token: String): Response<List<IoTDeviceResponse>>

    @DELETE("api/v1/monitoring/io-t-devices/{id}")
    suspend fun deleteIoTDevice(@Header("Authorization") token: String, @Path("id") id: Long): Response<Unit>

    @GET("api/v1/monitoring/notifications/user")
    suspend fun getUserNotifications(@Header("Authorization") token: String): Response<List<NotificationResponse>>

    @PUT("api/v1/monitoring/notifications/{id}/read")
    suspend fun markNotificationAsRead(@Header("Authorization") token: String, @Path("id") id: Long): Response<Unit>

    @POST("api/v1/monitoring/tasks/request")
    suspend fun requestTask(@Header("Authorization") token: String, @Body request: com.example.spacepulse.model.beans.TaskRequest): retrofit2.Response<com.example.spacepulse.model.beans.TaskResponse>

    @GET("api/v1/monitoring/tasks/space/{spaceId}")
    suspend fun getTasksBySpaceId(
        @Header("Authorization") token: String,
        @Path("spaceId") spaceId: Long
    ): Response<List<TaskResponse>>
    @DELETE("api/v1/monitoring/tasks/{id}")
    suspend fun deleteWorkItem(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): retrofit2.Response<Unit>

    @PUT("api/v1/monitoring/tasks/{id}/content")
    suspend fun updateTaskContent(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body resource: UpdateTaskContentRequest
    ): retrofit2.Response<com.example.spacepulse.model.beans.TaskResponse>
    @Multipart
    @POST("https://api.imgbb.com/1/upload")
    suspend fun uploadImageToImgBB(
        @Query("key") apiKey: String,
        @Part image: MultipartBody.Part
    ): retrofit2.Response<com.example.spacepulse.model.beans.ImgBBResponse>

    @PUT("api/v1/space/{id}/cancel")
    suspend fun cancelSpace(
        @Header("Authorization") token: String,
        @Path("id") spaceId: Long
    ): Response<Unit>

    @PUT("api/v1/space/{id}/complete")
    suspend fun completeSpace(
        @Header("Authorization") token: String,
        @Path("id") spaceId: Long
    ): Response<Unit>
}