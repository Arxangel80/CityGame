package com.example.citygame.data

import android.content.Context
import com.example.citygame.data.remote.ApiService
import com.example.citygame.data.remote.PersistentCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private var apiService: ApiService? = null

    fun provideApiService(context: Context, cookieJar: PersistentCookieJar): ApiService {
        if (apiService == null) {
            val okHttpClient = OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("http://192.168.0.17:5000/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            apiService = retrofit.create(ApiService::class.java)
        }
        return apiService!!
    }
}
