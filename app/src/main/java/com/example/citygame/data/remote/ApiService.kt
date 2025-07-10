package com.example.citygame.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response

data class LoginRequest(
    val name: String,
    val password: String
)

data class UserDto(
    val id: Int,
    val username: String,
)

data class LoginResponse(
    val status: String,
    val message: String,
    val user: UserDto? = null
)

interface ApiService {
    @POST("users/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>
    suspend fun registerUser(@Body request: LoginRequest): Response<LoginResponse>
}
