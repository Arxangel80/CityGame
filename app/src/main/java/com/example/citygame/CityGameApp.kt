package com.example.citygame

import TrackersManager
import android.app.Application
import android.content.Context
import com.example.citygame.utils.Quests
import com.example.citygame.data.NetworkModule
import com.example.citygame.data.remote.ApiService

class CityGameApp : Application() {
    val trackersManager by lazy { TrackersManager(this) }


    override fun onCreate() {
        super.onCreate()
        appContext = this
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}
