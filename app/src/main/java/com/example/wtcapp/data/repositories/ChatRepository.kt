package com.example.wtcapp.data.repositories

import com.example.wtcapp.data.models.ChatMessage
import com.example.wtcapp.data.models.MessageStatusUpdate
import com.example.wtcapp.data.remote.RetrofitClient
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ChatRepository(private val jwtToken: String) {
    private val _incomingMessages = MutableSharedFlow<ChatMessage>(
        replay = 0,
        extraBufferCapacity = 64
    )
    val incomingMessages = _incomingMessages.asSharedFlow()

    private val _statusUpdates = MutableSharedFlow<MessageStatusUpdate>(
        replay = 0,
        extraBufferCapacity = 64
    )
    val statusUpdates = _statusUpdates.asSharedFlow()

    private var hubConnection: HubConnection? = null
    private var connectedChatId: String? = null
    private var connectionReady = false

    private val hubUrl = "https://wtc2fase-net.onrender.com/hubs/chat"

    fun connect(chatId: String) {
        if (hubConnection != null && connectedChatId == chatId) {
            android.util.Log.d(
                "SignalR",
                "Conexao SignalR ja existe para chatId=$chatId state=${connectionStateLabel()}"
            )
            return
        }

        if (hubConnection != null && connectedChatId != chatId) {
            android.util.Log.d(
                "SignalR",
                "Chat mudou de $connectedChatId para $chatId. Desconectando conexao anterior."
            )
            disconnect(connectedChatId.orEmpty())
        }

        val token = normalizeToken(jwtToken)
        android.util.Log.d("SignalR", "Criando conexao SignalR")
        android.util.Log.d("SignalR", "Hub URL: $hubUrl")
        android.util.Log.d("SignalR", "Token vazio=${token.isBlank()}")

        val connection = HubConnectionBuilder
            .create(hubUrl)
            .withAccessTokenProvider(Single.just(token))
            .build()

        hubConnection = connection
        connectedChatId = chatId
        connectionReady = false

        connection.on(
            "ReceiveMessage",
            { id: String, messageChatId: String, senderId: String, text: String, sentAt: String, status: String ->
                val message = ChatMessage(
                    id = id,
                    chatId = messageChatId,
                    senderId = senderId,
                    text = text,
                    sentAt = sentAt,
                    status = status
                )

                if (message.chatId.isNotBlank() && message.chatId != connectedChatId) {
                    android.util.Log.d(
                        "SignalR",
                        "ReceiveMessage ignorado: messageChatId=${message.chatId}, connectedChatId=$connectedChatId"
                    )
                } else {
                    android.util.Log.d(
                        "SignalR",
                        "ReceiveMessage recebido args: id=$id chatId=$messageChatId senderId=$senderId status=$status state=${connectionStateLabel()}"
                    )
                    val emitted = _incomingMessages.tryEmit(message)
                    android.util.Log.d("SignalR", "ReceiveMessage tryEmit=$emitted")
                }
            },
            String::class.java,
            String::class.java,
            String::class.java,
            String::class.java,
            String::class.java,
            String::class.java
        )

        connection.on(
            "MessageStatusUpdated",
            { messageId: String, status: String ->
                android.util.Log.d(
                    "SignalR",
                    "MessageStatusUpdated recebido args: $messageId -> $status state=${connectionStateLabel()}"
                )
                val emitted = _statusUpdates.tryEmit(
                    MessageStatusUpdate(
                        messageId = messageId,
                        status = status
                    )
                )
                android.util.Log.d("SignalR", "MessageStatusUpdated tryEmit=$emitted")
            },
            String::class.java,
            String::class.java
        )

        android.util.Log.d("SignalR", "Listeners SignalR registrados")
        android.util.Log.d("SignalR", "Iniciando conexao. state=${connectionStateLabel()}")

        connection.start()
            .doOnComplete {
                connectionReady = true
                android.util.Log.d("SignalR", "Conexao iniciada. state=${connectionStateLabel()}")
                joinChat(chatId)
            }
            .doOnError { error ->
                connectionReady = false
                android.util.Log.e(
                    "SignalR",
                    "Erro de start. state=${connectionStateLabel()} message=${error.message}",
                    error
                )
            }
            .subscribe(
                { },
                { error ->
                    connectionReady = false
                    android.util.Log.e(
                        "SignalR",
                        "Subscribe error. state=${connectionStateLabel()} message=${error.message}",
                        error
                    )
                }
            )
    }

    fun disconnect(chatId: String) {
        val connection = hubConnection ?: run {
            android.util.Log.d("SignalR", "Disconnect ignorado: sem conexao ativa para chatId=$chatId")
            return
        }

        android.util.Log.d(
            "SignalR",
            "Disconnect chamado: chatId=$chatId connectedChatId=$connectedChatId ready=$connectionReady state=${connectionStateLabel()}"
        )

        if (connectionReady && connectedChatId == chatId) {
            leaveChat(chatId)
        } else {
            android.util.Log.d("SignalR", "LeaveChat ignorado: conexao nao pronta ou chatId diferente")
        }

        connectionReady = false
        hubConnection = null
        connectedChatId = null

        connection.stop()
            .subscribe(
                { android.util.Log.d("SignalR", "Conexao parada") },
                { error -> android.util.Log.e("SignalR", "Erro ao parar conexao: ${error.message}", error) }
            )
    }

    fun sendMessage(chatId: String, text: String): Boolean {
        android.util.Log.d(
            "SignalR",
            "SendMessage chamado: chatId=$chatId textBlank=${text.isBlank()} ready=$connectionReady state=${connectionStateLabel()}"
        )
        if (!canInvokeHub(chatId)) {
            android.util.Log.e("SignalR", "SendMessage bloqueado: conexao SignalR nao esta pronta para chatId=$chatId")
            return false
        }
        invokeHub("SendMessage", chatId, text)
        return true
    }

    fun confirmDelivery(messageId: String) {
        android.util.Log.d(
            "SignalR",
            "ConfirmDelivery chamado: messageId=$messageId ready=$connectionReady state=${connectionStateLabel()}"
        )
        if (!canInvokeHub(connectedChatId)) {
            android.util.Log.e("SignalR", "ConfirmDelivery bloqueado: conexao SignalR nao esta pronta")
            return
        }
        invokeHub("ConfirmDelivery", messageId)
    }

    fun markAsRead(chatId: String): Boolean {
        android.util.Log.d(
            "SignalR",
            "MarkAsRead chamado: chatId=$chatId ready=$connectionReady state=${connectionStateLabel()}"
        )
        if (!canInvokeHub(chatId)) {
            android.util.Log.e("SignalR", "MarkAsRead bloqueado: conexao SignalR nao esta pronta para chatId=$chatId")
            return false
        }
        invokeHub("MarkAsRead", chatId)
        return true
    }

    private fun joinChat(chatId: String) {
        android.util.Log.d("SignalR", "JoinChat chamado: chatId=$chatId state=${connectionStateLabel()}")
        invokeHub("JoinChat", chatId)
    }

    private fun leaveChat(chatId: String) {
        android.util.Log.d("SignalR", "LeaveChat chamado: chatId=$chatId state=${connectionStateLabel()}")
        invokeHub("LeaveChat", chatId)
    }

    private fun invokeHub(method: String, vararg args: Any) {
        val connection = hubConnection
        if (connection == null) {
            android.util.Log.e("SignalR", "$method nao enviado: hubConnection nula")
            return
        }

        android.util.Log.d("SignalR", "$method invocando. state=${connectionStateLabel()}")
        connection.invoke(method, *args)
            .subscribe(
                { android.util.Log.d("SignalR", "$method concluido. state=${connectionStateLabel()}") },
                { error -> android.util.Log.e("SignalR", "$method erro: ${error.message}", error) }
            )
    }

    suspend fun getHistory(chatId: String): List<ChatMessage> {
        return RetrofitClient.chatApi.getHistory("Bearer $jwtToken", chatId)
    }

    private fun normalizeToken(token: String): String {
        return token.removePrefix("Bearer ").trim()
    }

    private fun canInvokeHub(chatId: String?): Boolean {
        return hubConnection != null && connectionReady && connectedChatId == chatId
    }

    private fun connectionStateLabel(): String {
        return hubConnection?.connectionState?.toString() ?: "null"
    }
}
