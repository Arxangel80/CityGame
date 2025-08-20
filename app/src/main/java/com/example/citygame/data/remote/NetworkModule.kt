package com.example.citygame.data

import android.app.Application
import android.content.Context
import com.example.citygame.CityGameApp
import com.example.citygame.data.remote.ApiService
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
}

