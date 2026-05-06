package com.example.wtcapp.data.repositories

import com.example.wtcapp.data.models.ChatMessage
import com.example.wtcapp.data.remote.RetrofitClient
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ChatRepository(private val jwtToken: String) {

    private val _incomingMessages = MutableSharedFlow<ChatMessage>(replay = 1)
    val incomingMessages = _incomingMessages.asSharedFlow()

    private var hubConnection: HubConnection? = null

    fun connect(chatId: String) {
        hubConnection = HubConnectionBuilder
            .create("http://10.0.2.2:5255/hubs/chat")
            .withAccessTokenProvider(Single.just(jwtToken))
            .build()

        hubConnection!!.on("ReceiveMessage", { msg: ChatMessage ->
            _incomingMessages.tryEmit(msg)
        }, ChatMessage::class.java)

        hubConnection!!.start()
            .doOnComplete { joinChat(chatId) }
            .subscribe()
    }

    fun disconnect(chatId: String) {
        leaveChat(chatId)
        hubConnection?.stop()
    }

    fun sendMessage(chatId: String, text: String) {
        hubConnection?.invoke("SendMessage", chatId, text)
    }

    private fun joinChat(chatId: String) {
        hubConnection?.invoke("JoinChat", chatId)
    }

    private fun leaveChat(chatId: String) {
        hubConnection?.invoke("LeaveChat", chatId)
    }

    suspend fun getHistory(chatId: String): List<ChatMessage> {
        return RetrofitClient.chatApi.getHistory("Bearer $jwtToken", chatId)
    }
}
