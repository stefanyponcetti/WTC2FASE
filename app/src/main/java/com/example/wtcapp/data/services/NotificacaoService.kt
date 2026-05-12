package com.example.wtcapp.data.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.wtcapp.data.models.ComunicadoNotificacao
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import io.reactivex.rxjava3.core.Single

class NotificacaoService : Service() {

    private var hubConnection: HubConnection? = null
    private val channelId = "comunicados_channel"
    private val notificationId = 1001

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                notificationId,
                buildForegroundNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(notificationId, buildForegroundNotification())
        }

        val token = intent?.getStringExtra("token") ?: return START_NOT_STICKY

        connectSignalR(token)

        return START_STICKY
    }

    private fun connectSignalR(token: String) {
        if (hubConnection != null) return

        hubConnection = HubConnectionBuilder
            .create("http://10.0.2.2:5255/hubs/chat")
            .withAccessTokenProvider(Single.just(token))
            .build()

        hubConnection?.on(
            "NovoComunicado",
            { notificacao: ComunicadoNotificacao ->
                showNotification(notificacao)
            },
            ComunicadoNotificacao::class.java
        )

        hubConnection?.start()
            ?.doOnError { android.util.Log.e("NotifService", "Erro: ${it.message}") }
            ?.subscribe(
                {},
                { android.util.Log.e("NotifService", "Subscribe erro: ${it.message}") }
            )
    }

    private fun showNotification(notificacao: ComunicadoNotificacao) {
        val uniqueNotificationId = System.currentTimeMillis().toInt()

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(notificacao.titulo)
            .setContentText("Por: ${notificacao.autor}")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(notificacao.descricao)
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(uniqueNotificationId, notification)
    }

    private fun buildForegroundNotification(): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("WTC App")
            .setContentText("Aguardando comunicados...")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            channelId,
            "Comunicados",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Notificações de novos comunicados"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        hubConnection?.stop()
        hubConnection = null
    }
}
