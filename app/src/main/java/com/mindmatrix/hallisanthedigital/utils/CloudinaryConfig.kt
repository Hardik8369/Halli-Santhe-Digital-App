package com.mindmatrix.hallisanthedigital.utils

import android.content.Context
import com.cloudinary.android.MediaManager

object CloudinaryConfig {

    private var isInitialized = false

    fun init(context: Context) {
        if (!isInitialized) {
            val config = HashMap<String, String>()
            config["cloud_name"] = "dzgygn4lk"
            MediaManager.init(context, config)
            isInitialized = true
        }
    }
}