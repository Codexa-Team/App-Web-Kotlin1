package com.example.spacepulse.model.beans

import com.google.gson.annotations.SerializedName

// --- AUTHENTICATION ---
data class SignInRequest(
    val email: String,
    val password: String
)

data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String // "arrendador" | "arrendatario"
)

data class AuthenticatedUserResource(
    val id: Long,
    val name: String,
    val email: String,
    val roles: List<String>,
    val token: String
)

data class UserResource(
    val id: Long,
    val name: String,
    val email: String,
    val roles: List<String>
)

data class UpdateUserRequest(
    val name: String,
    val email: String
)

data class UpdatePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

// --- VEHICLES ---
data class VehicleResource(
    val id: Long,
    val brand: String,
    val model: String,
    val year: Int,
    @SerializedName(value = "pricePerDay", alternate = ["price_per_day"])
    val pricePerDay: Double,
    val status: String? = "available",
    @SerializedName(value = "imageUrl", alternate = ["image_url", "image"])
    val imageUrl: String? = null,
    @SerializedName(value = "ownerId", alternate = ["owner_id"])
    val ownerId: Long? = null,
    @SerializedName(value = "createdAt", alternate = ["created_at"])
    val createdAt: String? = null
)

data class CreateVehicleResource(
    val brand: String,
    val model: String,
    val year: Int,
    val pricePerDay: Double
)

// --- BOOKINGS ---
data class BookingResource(
    val id: Long,
    @SerializedName(value = "vehicleId", alternate = ["vehicle_id"])
    val vehicleId: Long,
    @SerializedName(value = "renterId", alternate = ["renter_id"])
    val renterId: Long,
    @SerializedName(value = "ownerId", alternate = ["owner_id"])
    val ownerId: Long,
    @SerializedName(value = "startDate", alternate = ["start_date"])
    val startDate: String,
    @SerializedName(value = "endDate", alternate = ["end_date"])
    val endDate: String,
    @SerializedName(value = "totalPrice", alternate = ["total_price"])
    val totalPrice: Double,
    val status: String, // "PENDING", "CONFIRMED", "CANCELLED", "REJECTED"
    @SerializedName(value = "createdAt", alternate = ["created_at"])
    val createdAt: String? = null
)

data class CreateBookingResource(
    val vehicleId: Long,
    val startDate: String,
    val endDate: String
)

// --- REVIEWS ---
data class ReviewResource(
    val id: Long,
    @SerializedName(value = "vehicleId", alternate = ["vehicle_id"])
    val vehicleId: Long,
    @SerializedName(value = "renterId", alternate = ["renter_id"])
    val renterId: Long,
    val rating: Int,
    val comment: String,
    @SerializedName(value = "createdAt", alternate = ["created_at"])
    val createdAt: String? = null
)

data class CreateReviewResource(
    val vehicleId: Long,
    val rating: Int,
    val comment: String
)
