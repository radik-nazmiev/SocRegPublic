package rnstudio.socreg.services

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import rnstudio.socreg.Notification
import rnstudio.socreg.R

class RegServices: Service() {
    private var notificationBuilder: android.app.Notification.Builder? = null

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification = Notification(
            this, "1", "Registration service",
            getString(R.string.task_at_work), getString(R.string.account_registration),
            NotificationManager.IMPORTANCE_HIGH, manager, null
        )

        notificationBuilder = notification.createNotif()
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)

        startForeground(1, notificationBuilder?.build())
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}