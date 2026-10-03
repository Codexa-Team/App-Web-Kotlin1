package com.example.spacepulse.viewmodel

import android.content.Context
import android.net.Uri
import com.example.spacepulse.model.client.RetrofitClient
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream

object ImgBBUploader {

    private const val API_KEY = "e50506198b39dafe03e32e9c2a22c069"
    suspend fun uploadPhoto(context: Context, imageUri: Uri): String? {
        return try {
            val file = uriToFile(context, imageUri) ?: return null

            val mediaType = "image/*".toMediaTypeOrNull()
            val requestFile = RequestBody.create(mediaType, file)

            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

            val response = RetrofitClient.webService.uploadImageToImgBB(API_KEY, body)

            if (response.isSuccessful) {
                response.body()?.data?.url
            } else {
                android.util.Log.e("IMGBB", "Error: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("IMGBB", "Fallo de red: ${e.message}")
            null
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File(context.cacheDir, "temp_img_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            tempFile
        } catch (e: Exception) {
            null
        }
    }
}