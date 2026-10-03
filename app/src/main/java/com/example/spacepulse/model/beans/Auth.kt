package com.example.spacepulse.model.beans

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val phone: String,
    val role: String,
    val photo: String
)

data class LoginResponse(
    val userId: String,
    val fullName: String,
    val email: String,
    val token: String
)

data class RegisterResponse(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String?,
    val role: String,
    val photo: String?,
    val createdAt: String?
)

data class PaymentMethodResponse(
    val id: String,
    val type: String,
    @SerializedName(value = "lastFour", alternate = ["last_four_digits", "lastFourDigits", "number", "cardNumber", "last4"])
    val lastFour: String?,
    val expiry: String,
    val cvv: String
)

data class UserProfileResponse(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String?,
    val role: String,
    val photo: String?,
    val paymentMethods: List<PaymentMethodResponse>
)

data class AddPaymentMethodRequest(
    val type: String,
    val number: String,
    val expiry: String,
    val cvv: String
)

data class SpaceResponse(
    val id: Long,
    val title: String,
    val description: String,
    val location: String,
    val homeownerId: String?,
    val spaceType: String?,
    val dimensionsSquareMeters: Double?,
    val estimatedBudget: Double?,
    val currency: String?,

    val images: List<String>,
    val status: String,
    val hasIot: Boolean
)

data class CreateSpaceRequest(
    val homeownerId: String,
    val title: String,
    val description: String,
    val location: String,
    val spaceType: String,
    val dimensionsSquareMeters: Double,
    val estimatedBudget: Double,
    val currency: String,
    val hasIot: Boolean,
    val images: List<String>
)

data class UpdateSpaceRequest(
    val title: String,
    val description: String,
    val location: String,
    val dimensionsSquareMeters: Double,
    val estimatedBudget: Double,
    val hasIot: Boolean,
    val images: List<String>,
    val status: String? = null
)

data class IoTDeviceResponse(
    val id: Long,
    val spaceId: Long,
    val type: String,
    val name: String,
    val serialNumber: String,
    val metricName: String?,
    val unit: String?,
    val minThreshold: Double?,
    val maxThreshold: Double?,
    val isOn: Boolean,
    val value: Double?,
    val timestamp: String?,
    val isInAlertState: Boolean?
)

data class CreateIoTDeviceRequest(
    val spaceId: Long,
    val type: String,
    val name: String,
    val serialNumber: String,
    val metricName: String,
    val unit: String,
    val minThreshold: Double,
    val maxThreshold: Double
)

data class UpdateIoTDeviceRequest(
    val name: String,
    val serialNumber: String
)

data class NotificationResponse(
    val id: Long? = null,
    val spaceId: Long? = null,
    val title: String? = null,
    val message: String? = null,
    val isRead: Boolean? = null,
    val createdAt: String? = null
)

data class TaskRequest(
    val spaceId: Long,
    val title: String,
    val description: String,
    val photoUrl: String
)

data class TaskResponse(
    val id: Long,
    val spaceId: Long,
    val title: String,
    val description: String,
    val photoUrl: String,
    val status: String
)

data class UpdateTaskContentRequest(
    val title: String,
    val description: String,
    val photoUrl: String = ""
)

data class ImgBBResponse(
    val data: ImgBBData?
)

data class ImgBBData(
    val url: String?
)