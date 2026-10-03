package com.example.spacepulse.view

fun formatNotificationDate(createdAt: String?): String {
    if (createdAt.isNullOrBlank()) return "Sin fecha"
    return createdAt
        .replace("T", " ")
        .take(16)
}