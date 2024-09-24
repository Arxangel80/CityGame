package com.example.citygame.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.GET

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
    val access_token: String? = null,
    val refresh_token: String? = null
)

data class SessionData(
    val status: String,
    val session_active: Boolean,
    val quest_name: String? = null
)

data class CreateSessionRequest(val game_type_name: String = "CAMPUSGAME")

data class CreateSessionResponse(
    val status: String,
)

interface ApiService {
    @POST("users/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    @POST("users/signup")
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("/sessions/current")
    suspend fun getCurrentSession(): Response<SessionData>

    @POST("sessions/create")
    suspend fun createSession(@Body request: CreateSessionRequest): Response<CreateSessionResponse>

    @POST("user/logout")
    suspend fun userLogout(): Response<Unit>
}
