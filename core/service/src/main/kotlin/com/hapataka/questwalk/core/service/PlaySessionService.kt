package com.hapataka.questwalk.core.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.hapataka.questwalk.core.domain.usecase.GetPlaySessionUseCase
import com.hapataka.questwalk.core.domain.usecase.IncrementStepUseCase
import com.hapataka.questwalk.core.model.PlayState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PlaySessionService : Service() {

    @Inject
    lateinit var incrementStepUseCase: IncrementStepUseCase

    @Inject
    lateinit var getPlaySessionUseCase: GetPlaySessionUseCase

    @Inject
    lateinit var stepSensorManager: StepSensorManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "play_session_channel"

        fun start(context: Context) {
            val intent = Intent(context, PlaySessionService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, PlaySessionService::class.java))
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification(0L, 0L, 0f))
        startStepSensor()
        observeSessionState()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        stepSensorManager.stop()
        serviceScope.cancel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "퀘스트 진행",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "퀘스트 진행 상태를 표시합니다"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(steps: Long, duration: Long, distance: Float): Notification {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = launchIntent?.let {
            PendingIntent.getActivity(
                this,
                0,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        val formattedDuration = formatDuration(duration)
        val formattedDistance = formatDistance(distance)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("퀘스트 진행 중")
            .setContentText("$steps 걸음 | $formattedDuration | $formattedDistance")
            .setSmallIcon(R.drawable.ic_notification)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    private fun startStepSensor() {
        if (!stepSensorManager.isAvailable()) {
            Toast.makeText(this, "이 기기에서는 걸음 센서가 지원되지 않습니다", Toast.LENGTH_LONG).show()
            return
        }

        stepSensorManager.start {
            incrementStepUseCase()
        }
    }

    private fun observeSessionState() {
        serviceScope.launch {
            getPlaySessionUseCase().collectLatest { session ->
                if (session.playState == PlayState.STOPPED) {
                    stopSelf()
                    return@collectLatest
                }

                val notification = createNotification(
                    steps = session.steps,
                    duration = session.duration,
                    distance = session.distance
                )
                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager.notify(NOTIFICATION_ID, notification)
            }
        }
    }

    private fun formatDuration(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, secs)
        } else {
            String.format("%02d:%02d", minutes, secs)
        }
    }

    private fun formatDistance(meters: Float): String {
        return if (meters >= 1000) {
            String.format("%.1fkm", meters / 1000)
        } else {
            String.format("%.0fm", meters)
        }
    }
}
