package com.example.citygame.data

import android.app.Application
import android.content.Context
import com.example.citygame.CityGameApp
import com.example.citygame.data.remote.ApiService
import io.socket.client.IO
import io.socket.client.IO.socket
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object NetworkModule {
    private const val BASE_URL = "http://192.168.0.13:5000/"

    private var authToken: String? = null

    fun setToken(token: String) {
        authToken = token
    }

    private class AuthInterceptor(private val context: Context) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            val builder = originalRequest.newBuilder()

            authToken?.let {
                builder.addHeader("Authorization", "Bearer $it")
            }

            return chain.proceed(builder.build())
        }
    }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(CityGameApp.appContext))
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val apiService = retrofit.create(ApiService::class.java)

    // SocketIO
    private var socket: Socket? = null

    private val socketScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _socketEvents = MutableSharedFlow<String>(extraBufferCapacity = 10)
    val socketEvents: SharedFlow<String> = _socketEvents.asSharedFlow()

    fun connectSocket() {
        if (socket != null && socket!!.connected()) return

        val opts = IO.Options().apply {
            query = "token=${authToken}"
        }

        socket = IO.socket(BASE_URL.removeSuffix("/"), opts)
        socket?.connect()

        socket?.on(Socket.EVENT_CONNECT) {
            socketScope.launch {
                _socketEvents.emit("Socket connected")
            }
        }

        socket?.on("SessionCreated") { args ->
            val message = args.joinToString()
            socketScope.launch {
                _socketEvents.emit("SessionCreated: $message")
            }
        }

        socket?.on(Socket.EVENT_DISCONNECT) {
            socketScope.launch {
                _socketEvents.emit("Socket disconnected")
            }
        }
    }

    fun disconnectSocket() {
        socket?.disconnect()
        socket = null
    }

    fun emit(event: String, data: Any) {
        socket?.emit(event, data)
    }
}

