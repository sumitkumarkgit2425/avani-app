package com.example.navya.data.repository

import android.content.Context
import com.example.navya.data.local.entity.UserEntity
import com.example.navya.data.models.UserDto
import com.example.navya.data.remote.ApiService
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class UserRepository
@Inject
constructor(
        private val apiService: ApiService,
        private val userDao: com.example.navya.data.local.dao.UserDao,
        @ApplicationContext private val context: Context
) {

    suspend fun syncUser(firebaseUser: FirebaseUser) {
        try {
            val users = apiService.getUser("eq.${firebaseUser.uid}")

            if (users.isEmpty()) {
                val newUser =
                        UserDto(
                                id = firebaseUser.uid,
                                email = firebaseUser.email ?: "",
                                display_name = firebaseUser.displayName ?: "User",
                                photo_url = firebaseUser.photoUrl?.toString()
                        )
                apiService.createUser(newUser)

                userDao.insertUser(
                        com.example.navya.data.local.entity.UserEntity(
                                id = newUser.id,
                                email = newUser.email,
                                display_name = newUser.display_name,
                                photo_url = newUser.photo_url
                        )
                )
            } else {

                val existingLocalResult = userDao.getUser(firebaseUser.uid).firstOrNull()
                val existingLocalPath = existingLocalResult?.local_photo_path

                val existing = users.first()

                var finalLocalPath = existingLocalPath
                if (finalLocalPath == null && !existing.photo_url.isNullOrEmpty()) {
                    finalLocalPath = downloadProfileImage(existing.photo_url, existing.id)
                }

                userDao.insertUser(
                        UserEntity(
                                id = existing.id,
                                email = existing.email,
                                display_name = existing.display_name,
                                photo_url = existing.photo_url,
                                local_photo_path = finalLocalPath
                        )
                )
            }
        } catch (e: Exception) {}
    }

    fun getUserProfile(userId: String): kotlinx.coroutines.flow.Flow<UserDto?> {
        return userDao.getUser(userId)
                .map { localUser ->
                    localUser?.let {
                        val finalUrl =
                                if (!it.local_photo_path.isNullOrEmpty() &&
                                                java.io.File(it.local_photo_path).exists()
                                ) {
                                    "file://${it.local_photo_path}"
                                } else {
                                    it.photo_url
                                }

                        UserDto(
                                id = it.id,
                                email = it.email,
                                display_name = it.display_name,
                                photo_url = finalUrl
                        )
                    }
                }
                .onStart { refreshUserProfile(userId) }
    }

    private suspend fun refreshUserProfile(userId: String) {
        try {
            val users = apiService.getUser("eq.$userId")
            val remoteUser = users.firstOrNull()
            if (remoteUser != null) {
                val existingLocalResult = userDao.getUser(userId).firstOrNull()
                var existingLocalPath = existingLocalResult?.local_photo_path
                if (existingLocalPath == null && !remoteUser.photo_url.isNullOrEmpty()) {
                    existingLocalPath = downloadProfileImage(remoteUser.photo_url, remoteUser.id)
                }

                userDao.insertUser(
                        UserEntity(
                                id = remoteUser.id,
                                email = remoteUser.email,
                                display_name = remoteUser.display_name,
                                photo_url = remoteUser.photo_url,
                                local_photo_path = existingLocalPath
                        )
                )
            }
        } catch (e: Exception) {}
    }

    suspend fun updateUserProfile(user: UserDto, token: String) {
        val authHeader = if (token.startsWith("Bearer ")) token else "Bearer $token"
        apiService.updateUser(authHeader, "eq.${user.id}", user)
    }

    suspend fun uploadProfileImage(
            uri: android.net.Uri,
            userId: String,
            context: android.content.Context,
            currentDisplayName: String,
            currentEmail: String
    ): String {
        return try {
            val contentResolver = context.contentResolver
            val inputStream =
                    contentResolver.openInputStream(uri) ?: throw Exception("Cannot open URI")
            val bytes = inputStream.readBytes()
            inputStream.close()

            val requestFile = okhttp3.RequestBody.create("image/jpeg".toMediaTypeOrNull(), bytes)

            val body = okhttp3.MultipartBody.Part.createFormData("file", "profile.jpg", requestFile)

            val fileName = "$userId.jpg"
            val baseUrl = com.example.navya.utils.SupabaseConfig.SUPABASE_URL
            val cleanBaseUrl = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl
            val fullStorageUrl = "$cleanBaseUrl/storage/v1/object/avatars/$fileName"

            apiService.uploadFile(fullStorageUrl, body)

            val publicUrl =
                    "${com.example.navya.utils.SupabaseConfig.SUPABASE_URL}/storage/v1/object/public/avatars/$fileName"
            val versionedUrl = "$publicUrl?t=${System.currentTimeMillis()}"

            val userDto =
                    UserDto(
                            id = userId,
                            email = currentEmail,
                            display_name = currentDisplayName,
                            photo_url = versionedUrl
                    )

            try {
                apiService.updateUser(
                        "Bearer ${com.example.navya.utils.SupabaseConfig.SUPABASE_ANON_KEY}",
                        "eq.$userId",
                        userDto
                )
            } catch (e: Exception) {}

            val localFile = java.io.File(context.filesDir, "profile_${userId}.jpg")
            localFile.writeBytes(bytes)

            userDao.insertUser(
                    UserEntity(
                            id = userId,
                            email = currentEmail,
                            display_name = currentDisplayName,
                            photo_url = versionedUrl,
                            local_photo_path = localFile.absolutePath
                    )
            )

            return versionedUrl
        } catch (e: Exception) {

            throw e
        }
    }

    private suspend fun downloadProfileImage(url: String, userId: String): String? {
        return try {
            withContext(Dispatchers.IO) {
                val request = okhttp3.Request.Builder().url(url).build()
                val response = okhttp3.OkHttpClient().newCall(request).execute()
                if (response.isSuccessful) {
                    val bytes = response.body?.bytes()
                    if (bytes != null) {
                        val localFile = java.io.File(context.filesDir, "profile_${userId}.jpg")
                        localFile.writeBytes(bytes)
                        localFile.absolutePath
                    } else null
                } else null
            }
        } catch (e: Exception) {

            null
        }
    }
}
