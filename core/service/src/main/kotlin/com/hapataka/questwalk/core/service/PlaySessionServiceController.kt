package com.hapataka.questwalk.core.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaySessionServiceController @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun start() {
        val intent = Intent(context, PlaySessionService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    fun stop() {
        context.stopService(Intent(context, PlaySessionService::class.java))
    }
}
