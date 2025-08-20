package com.example.citygame

import TrackersManager
import SocketIOManager
import android.app.Application
import android.content.Context
import com.example.citygame.utils.Quests
import com.example.citygame.data.NetworkModule
import com.example.citygame.data.remote.ApiService

class CityGameApp : Application() {
    lateinit var siManager: SocketIOManager
    val trackersManager by lazy { TrackersManager(this) }


    override fun onCreate() {
        super.onCreate()
        appContext = this
        siManager = SocketIOManager("http://192.168.0.17:5000")
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}
