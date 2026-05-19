package com.example.wtcapp.data.repositories

import android.util.Log
import com.example.wtcapp.data.models.ChatMessage
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ChatListRealtimeRepository(private val jwtToken: String) {
    private val _incomingMessages = MutableSharedFlow<ChatMessage>(
        replay = 0,
        extraBufferCapacity = 64
    )
    val incomingMessages = _incomingMessages.asSharedFlow()

    private var hubConnection: HubConnection? = null
    private var connectionReady = false

    private val hubUrl = "http://10.0.2.2:5255/hubs/chat"

    fun connect() {
        if (hubConnection != null && connectionStateLabel() != "DISCONNECTED") {
            Log.d(TAG, "ChatList realtime ja conectado. state=${connectionStateLabel()}")
            return
        }

        val token = normalizeToken(jwtToken)
        if (token.isBlank()) {
            Log.e(TAG, "ChatList realtime nao conectado: token vazio")
            return
        }

        Log.d(TAG, "ChatList realtime conectando")
        Log.d(TAG, "ChatList hub URL=$hubUrl")

        val connection = HubConnectionBuilder
            .create(hubUrl)
            .withAccessTokenProvider(Single.just(token))
            .build()

        hubConnection = connection
        connectionReady = false

        connection.on(
            "ReceiveMessage",
            { id: String, chatId: String, senderId: String, senderName: String, text: String, sentAt: String, status: String ->
                val message = ChatMessage(
                    id = id,
                    chatId = chatId,
                    senderId = senderId,
                    senderName = senderName,
                    text = text,
                    sentAt = sentAt,
                    status = status
                )

                Log.d(
                    TAG,
                    "ChatList ReceiveMessage recebido: id=$id chatId=$chatId senderId=$senderId senderName=$senderName status=$status state=${connectionStateLabel()}"
                )

                val emitted = _incomingMessages.tryEmit(message)
                Log.d(TAG, "ChatList ReceiveMessage tryEmit=$emitted")
                Log.d(TAG, "ChatList MarkAsRead NAO chamado")
            },
            String::class.java,
            String::class.java,
            String::class.java,
            String::class.java,
            String::class.java,
            String::class.java,
            String::class.java
        )

        Log.d(TAG, "ChatList realtime listeners registrados")

        connection.start()
            .doOnComplete {
                connectionReady = true
                Log.d(TAG, "ChatList realtime conectado. state=${connectionStateLabel()}")
            }
            .doOnError { error ->
                connectionReady = false
                Log.e(TAG, "ChatList realtime start erro: ${error.message}", error)
            }
            .subscribe(
                { },
                { error ->
                    connectionReady = false
                    Log.e(TAG, "ChatList realtime subscribe erro: ${error.message}", error)
                }
            )
    }

    fun confirmDelivery(messageId: String) {
        Log.d(
            TAG,
            "ChatList ConfirmDelivery chamado: messageId=$messageId ready=$connectionReady state=${connectionStateLabel()}"
        )

        if (!connectionReady || hubConnection == null) {
            Log.e(TAG, "ChatList ConfirmDelivery bloqueado: conexao nao esta pronta")
            return
        }

        invokeHub("ConfirmDelivery", messageId)
    }

    fun disconnect() {
        val connection = hubConnection ?: run {
            Log.d(TAG, "ChatList realtime disconnect ignorado: sem conexao")
            return
        }

        connectionReady = false
        hubConnection = null

        connection.stop()
            .subscribe(
                { Log.d(TAG, "ChatList realtime desconectado") },
                { error -> Log.e(TAG, "ChatList realtime stop erro: ${error.message}", error) }
            )
    }

    private fun invokeHub(method: String, vararg args: Any) {
        val connection = hubConnection
        if (connection == null) {
            Log.e(TAG, "ChatList $method nao enviado: hubConnection nula")
            return
        }

        Log.d(TAG, "ChatList $method invocando. state=${connectionStateLabel()}")
        connection.invoke(method, *args)
            .subscribe(
                { Log.d(TAG, "ChatList $method concluido. state=${connectionStateLabel()}") },
                { error -> Log.e(TAG, "ChatList $method erro: ${error.message}", error) }
            )
    }

    private fun normalizeToken(token: String): String {
        return token.removePrefix("Bearer ").trim()
    }

    private fun connectionStateLabel(): String {
        return hubConnection?.connectionState?.toString() ?: "null"
    }

    companion object {
        private const val TAG = "ChatListRealtime"
    }
}
