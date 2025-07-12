package com.example.citygame

import QuestSessionManager
import SocketIOManager
import android.app.Application
import com.example.citygame.data.NetworkModule
import com.example.citygame.data.remote.ApiService
import com.example.citygame.data.remote.PersistentCookieJar

class CityGameApp : Application() {
    lateinit var cookieJar: PersistentCookieJar
    lateinit var siManager: SocketIOManager
    lateinit var apiService: ApiService
    val questSessionManager by lazy { QuestSessionManager(this) }


    override fun onCreate() {
        super.onCreate()
        cookieJar = PersistentCookieJar(this)
        apiService = NetworkModule.provideApiService(this, cookieJar)
        siManager = SocketIOManager("http://192.168.0.17:5000", cookieJar)
    }
}
