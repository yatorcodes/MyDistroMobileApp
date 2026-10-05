package com.emmanuelyator.mydistro.feature.auth.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
}

data class LoginRequest(
    val identifier: String,
    val password: String
)

data class AuthResponse(
    val accessToken: String,
    val user: UserResponseDto
)

data class UserResponseDto(
    val id: String,
    val role: String,
    val fullName: String
)
