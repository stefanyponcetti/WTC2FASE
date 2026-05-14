package com.example.wtcapp.data.services

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.wtcapp.data.models.ComunicadoNotificacao
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import io.reactivex.rxjava3.core.Single

class NotificacaoService : Service() {

    private var hubConnection: HubConnection? = null
    private var tipoCliente: String = ""

    private val serviceChannelId = "notificacao_service_channel"
    private val comunicadosChannelId = "comunicados_channel_high"
    private val hubUrl = "https://wtc2fase-net.onrender.com/hubs/chat"

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        createNotificationChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val token = intent?.getStringExtra("token").orEmpty()
        tipoCliente = intent?.getStringExtra("tipoCliente")
            ?.takeIf { it.isNotBlank() }
            ?: tipoCliente

        Log.d(TAG, "onStartCommand")
        Log.d(TAG, "token blank=${token.isBlank()}")
        Log.d(TAG, "tipoCliente recebido=$tipoCliente")

        if (token.isBlank()) {
            Log.e(TAG, "Servico nao iniciado: token vazio")
            stopSelf()
            return START_NOT_STICKY
        }

        if (tipoCliente.isBlank()) {
            Log.e(TAG, "Servico nao iniciado: tipoCliente vazio")
            stopSelf()
            return START_NOT_STICKY
        }

        startForeground(SERVICE_NOTIFICATION_ID, createServiceNotification())
        Log.d(TAG, "startForeground chamado")

        connectSignalR(token)

        return START_STICKY
    }

    private fun connectSignalR(token: String) {
        val existingConnection = hubConnection
        if (existingConnection != null) {
            val state = connectionStateLabel()
            if (state != "DISCONNECTED") {
                Log.d(TAG, "Conexao SignalR ja existe. state=$state")
                return
            }

            Log.d(TAG, "Conexao antiga estava parada. Recriando SignalR.")
            hubConnection = null
        }

        val normalizedToken = normalizeToken(token)
        if (normalizedToken.isBlank()) {
            Log.e(TAG, "SignalR nao iniciado: token normalizado vazio")
            stopSelf()
            return
        }

        Log.d(TAG, "URL do hub=$hubUrl")
        Log.d(TAG, "Criando conexao SignalR")

        val connection = HubConnectionBuilder
            .create(hubUrl)
            .withAccessTokenProvider(Single.just(normalizedToken))
            .build()

        hubConnection = connection

        connection.on(
            "NovoComunicado",
            { id: String, titulo: String, descricao: String, autor: String, destinatarios: String ->
                val notificacao = ComunicadoNotificacao(
                    id = id,
                    titulo = titulo,
                    descricao = descricao,
                    autor = autor,
                    destinatarios = destinatarios
                )

                Log.d(
                    TAG,
                    "NovoComunicado recebido args: id=$id titulo=$titulo destinatarios=$destinatarios tipoCliente=$tipoCliente state=${connectionStateLabel()}"
                )

                val passouNoFiltro = tipoCliente.isBlank() ||
                    podeVisualizarComunicado(destinatarios, tipoCliente)

                Log.d(TAG, "passou no filtro=$passouNoFiltro")
                if (passouNoFiltro) {
                    showNotification(notificacao)
                }
            },
            String::class.java,
            String::class.java,
            String::class.java,
            String::class.java,
            String::class.java
        )

        Log.d(TAG, "Listeners SignalR registrados")

        connection.start()
            .doOnComplete {
                Log.d(TAG, "hubConnection.start sucesso. state=${connectionStateLabel()}")
            }
            .doOnError { error ->
                Log.e(TAG, "hubConnection.start erro: ${error.message}", error)
            }
            .subscribe(
                { },
                { error ->
                    Log.e(TAG, "hubConnection.start subscribe erro: ${error.message}", error)
                }
            )
    }

    private fun showNotification(notificacao: ComunicadoNotificacao) {
        Log.d(TAG, "showNotification chamado")

        if (!canPostNotifications()) {
            Log.e(TAG, "Permissao POST_NOTIFICATIONS negada")
            return
        }

        val uniqueNotificationId = System.currentTimeMillis().toInt()

        val notification = NotificationCompat.Builder(this, comunicadosChannelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(notificacao.titulo)
            .setContentText("Por: ${notificacao.autor}")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(notificacao.descricao)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(uniqueNotificationId, notification)
    }

    private fun createServiceNotification(): Notification {
        return NotificationCompat.Builder(this, serviceChannelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Notificações ativas")
            .setContentText("Você receberá novos comunicados em tempo real.")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val serviceChannel = NotificationChannel(
            serviceChannelId,
            "Serviço de notificações",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Mantém a conexão de notificações em tempo real ativa."
        }

        val comunicadosChannel = NotificationChannel(
            comunicadosChannelId,
            "Comunicados",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificações de novos comunicados"
            enableVibration(true)
            enableLights(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
        manager.createNotificationChannel(comunicadosChannel)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        hubConnection?.stop()
        hubConnection = null
        super.onDestroy()
    }

    private fun canPostNotifications(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true

        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun normalizeToken(token: String): String {
        return token.removePrefix("Bearer ").trim()
    }

    private fun connectionStateLabel(): String {
        return hubConnection?.connectionState?.toString() ?: "null"
    }

    private fun podeVisualizarComunicado(destinatarios: String?, tipoCliente: String): Boolean {
        val dest = destinatarios?.trim()?.lowercase()
        val tipo = tipoCliente.trim().lowercase()

        return dest == "todos" || dest == tipo
    }

    companion object {
        private const val TAG = "NotifService"
        private const val SERVICE_NOTIFICATION_ID = 1001
    }
}
