package com.example.citygame.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.Header

data class LoginRequest(
    val name: String,
    val password: String
)

data class LoginResponse(
    val status: String,
    val access_token: String? = null,
    val refresh_token: String? = null,
    val message: String? = null
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class RegisterResponse(
    val status: String,
    val message: String,
    val user: UserData? = null,
    val access_token: String? = null,
    val refresh_token: String? = null
)

data class CreateSessionResponse(
    val status: String,
)

data class CreateSessionRequest(val game_type_name: String = "CAMPUSGAME")

data class UserData(
    val id: Int,
    val username: String,
)

interface ApiService {
    @POST("users/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    @POST("users/signup")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("sessions/create")
    suspend fun createSession(@Body request: CreateSessionRequest): Response<CreateSessionResponse>
}
