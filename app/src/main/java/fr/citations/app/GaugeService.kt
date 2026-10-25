package fr.citations.app

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.time.LocalDate
import java.time.ZoneId

/**
 * Service au premier plan : il garde la notification permanente de l'objectif du jour
 * et la remet à zéro à minuit.
 */
class GaugeService : Service() {

    private val dayChanged = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            update()
            scheduleMidnight()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(CHANNEL, "Objectif du jour", NotificationManager.IMPORTANCE_LOW).apply {
            description = "Nombre de citations révisées aujourd’hui et palier atteint"
            setShowBadge(false)
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_DATE_CHANGED)
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
        }
        ContextCompat.registerReceiver(this, dayChanged, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = build()
        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(ID, notification)
        }
        scheduleMidnight()
        return START_STICKY
    }

    override fun onDestroy() {
        unregisterReceiver(dayChanged)
        super.onDestroy()
    }

    private fun update() {
        getSystemService(NotificationManager::class.java).notify(ID, build())
    }

    private fun build(): Notification {
        val n = Gauge.today(this)
        val open = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL)
            .setSmallIcon(R.drawable.ic_stat)
            .setContentTitle(Gauge.title(n))
            .setContentText(Gauge.line(n))
            .setStyle(NotificationCompat.BigTextStyle().bigText(Gauge.line(n) + "\n" + Gauge.tiersText()))
            .setProgress(Gauge.MAX, n.coerceAtMost(Gauge.MAX), false)
            .setColor(0xFF345995.toInt())
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setContentIntent(open)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
    }

    /** Programme une mise à jour juste après minuit pour remettre la jauge à zéro. */
    private fun scheduleMidnight() {
        val at = LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() + 1000
        val pending = PendingIntent.getForegroundService(
            this, 1, Intent(this, GaugeService::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        getSystemService(AlarmManager::class.java).setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pending)
    }

    companion object {
        private const val CHANNEL = "gauge"
        private const val ID = 1
    }
}
