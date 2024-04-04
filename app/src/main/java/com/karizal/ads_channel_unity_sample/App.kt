package com.karizal.ads_channel_unity_sample

import android.app.Application
import com.karizal.ads_channel_unity.UnityConst

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        UnityConst.init(this, Const.unityData)
    }
}