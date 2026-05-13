package com.mindmatrix.hallisanthedigital

import android.app.Application
import com.mindmatrix.hallisanthedigital.utils.CloudinaryConfig

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CloudinaryConfig.init(this)
    }
}