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
        replay = 1,
        extraBufferCapacity = 10
    )
    val incomingMessages = _incomingMessages.asSharedFlow()

    private val _statusUpdates = MutableSharedFlow<MessageStatusUpdate>(
        replay = 0,
        extraBufferCapacity = 20
    )
    val statusUpdates = _statusUpdates.asSharedFlow()

    private var hubConnection: HubConnection? = null

    fun connect(chatId: String) {
        hubConnection = HubConnectionBuilder
            .create("http://10.0.2.2:5255/hubs/chat")
            .withAccessTokenProvider(Single.just(jwtToken))
            .build()

        hubConnection!!.on("ReceiveMessage", { msg: ChatMessage ->
            android.util.Log.d("SignalR", "ReceiveMessage recebido: id=${msg.id}, status=${msg.status}")
            _incomingMessages.tryEmit(msg)
        }, ChatMessage::class.java)

        hubConnection!!.on("MessageStatusUpdated", { update: MessageStatusUpdate ->
            android.util.Log.d("SignalR", "MessageStatusUpdated recebido: ${update.messageId} -> ${update.status}")
            _statusUpdates.tryEmit(update)
        }, MessageStatusUpdate::class.java)

        hubConnection!!.start()
            .doOnComplete {
                android.util.Log.d("SignalR", "Conectado ao chat $chatId")
                joinChat(chatId)
                markAsRead(chatId)
            }
            .doOnError { error ->
                android.util.Log.e("SignalR", "Erro conexao: ${error.message}")
            }
            .subscribe(
                { },
                { error -> android.util.Log.e("SignalR", "Subscribe error: ${error.message}") }
            )
    }

    fun disconnect(chatId: String) {
        leaveChat(chatId)
        hubConnection?.stop()
    }

    fun sendMessage(chatId: String, text: String) {
        invokeHub("SendMessage", chatId, text)
    }

    fun confirmDelivery(messageId: String) {
        android.util.Log.d("SignalR", "ConfirmDelivery enviado: messageId=$messageId")
        invokeHub("ConfirmDelivery", messageId)
    }

    fun markAsRead(chatId: String) {
        android.util.Log.d("SignalR", "MarkAsRead enviado: chatId=$chatId")
        invokeHub("MarkAsRead", chatId)
    }

    private fun joinChat(chatId: String) {
        invokeHub("JoinChat", chatId)
    }

    private fun leaveChat(chatId: String) {
        invokeHub("LeaveChat", chatId)
    }

    private fun invokeHub(method: String, vararg args: Any) {
        hubConnection?.invoke(method, *args)
            ?.subscribe(
                { android.util.Log.d("SignalR", "$method concluido") },
                { error -> android.util.Log.e("SignalR", "$method erro: ${error.message}") }
            )
    }

    suspend fun getHistory(chatId: String): List<ChatMessage> {
        return RetrofitClient.chatApi.getHistory("Bearer $jwtToken", chatId)
    }
}
