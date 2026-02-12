package com.example.navya.data.remote

import com.example.navya.data.local.entity.PlantEntity
import com.example.navya.data.models.OwnedPlantDto
import com.example.navya.data.models.ReminderDto
import com.example.navya.data.models.UserDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("plants")
    suspend fun getPlants(): List<PlantEntity>

    @GET("reminders")
    suspend fun getReminders(@Query("user_id") userId: String): List<ReminderDto>

    @POST("reminders")
    suspend fun addReminder(@Body reminder: ReminderDto)

    @PATCH("reminders")
    suspend fun updateReminder(@Query("id") id: String, @Body reminder: ReminderDto)

    @DELETE("reminders")
    suspend fun deleteReminder(@Query("plant_id") plantId: String)

    @GET("users")
    suspend fun getUser(@Query("id") id: String): List<UserDto>

    @POST("users")
    suspend fun createUser(@Body user: UserDto)

    @PATCH("users")
    suspend fun updateUser(
        @retrofit2.http.Header("Authorization") token: String,
        @Query("id") id: String, 
        @Body user: UserDto
    )

    @GET("owned_plants")
    suspend fun getOwnedPlants(@Query("user_id") userId: String): List<OwnedPlantDto>

    @POST("owned_plants")
    suspend fun addOwnedPlant(@Body plant: OwnedPlantDto)

    @retrofit2.http.Multipart
    @POST
    suspend fun uploadFile(
        @retrofit2.http.Url url: String,
        @retrofit2.http.Part file: okhttp3.MultipartBody.Part,
        @retrofit2.http.Header("x-upsert") upsert: String = "true"
    )
}
