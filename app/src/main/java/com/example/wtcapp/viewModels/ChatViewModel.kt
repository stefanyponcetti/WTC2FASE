package com.example.wtcapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wtcapp.data.models.ChatMessage
import com.example.wtcapp.data.repositories.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChatViewModel(
    private val chatId: String,
    private val currentUserId: String,
    private val jwtToken: String
) : ViewModel() {
    private val repository = ChatRepository(jwtToken)
    private val pendingStatusUpdates = mutableMapOf<String, String>()
    private var isChatScreenActive = false
    private var markAsReadRetryJob: Job? = null

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHistory()
        collectMessages()
        connectSignalR()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val history = repository.getHistory(chatId)
                    .map { message -> message.copy(status = normalizeStatus(message.status)) }
                val historyIds = history.map { message -> message.id }.toSet()

                _uiState.update { state ->
                    state.copy(
                        messages = history + state.messages.filter { message -> message.id !in historyIds },
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun collectMessages() {
        viewModelScope.launch {
            repository.incomingMessages.collect { newMessage ->
                val status = pendingStatusUpdates.remove(newMessage.id) ?: normalizeStatus(newMessage.status)
                val normalizedMessage = newMessage.copy(status = status)
                android.util.Log.d(
                    "ChatVM",
                    "ReceiveMessage recebido na UI: id=${normalizedMessage.id}, sender=${normalizedMessage.senderId}"
                )
                var shouldAcknowledge = false

                _uiState.update { state ->
                    if (state.messages.any { message -> message.id == normalizedMessage.id }) return@update state
                    val optimisticIndex = state.messages.indexOfFirst { message ->
                        message.id.startsWith(OPTIMISTIC_MESSAGE_PREFIX) &&
                            message.senderId == normalizedMessage.senderId &&
                            message.text == normalizedMessage.text
                    }
                    if (optimisticIndex >= 0) {
                        val updatedMessages = state.messages.toMutableList()
                        updatedMessages[optimisticIndex] = normalizedMessage
                        return@update state.copy(messages = updatedMessages)
                    }
                    shouldAcknowledge = normalizedMessage.senderId != currentUserId
                    state.copy(messages = state.messages + normalizedMessage)
                }

                if (shouldAcknowledge) {
                    android.util.Log.d(
                        "ChatVM",
                        "ConfirmDelivery chamado porque mensagem chegou: messageId=${normalizedMessage.id}"
                    )
                    repository.confirmDelivery(normalizedMessage.id)

                    when {
                        !isChatScreenActive -> {
                            android.util.Log.d(
                                "ChatVM",
                                "MarkAsRead ignorado porque ChatScreen nao esta ativa: messageId=${normalizedMessage.id}"
                            )
                        }

                        normalizedMessage.chatId != chatId -> {
                            android.util.Log.d(
                                "ChatVM",
                                "MarkAsRead ignorado porque mensagem e de outro chat: messageChatId=${normalizedMessage.chatId}, currentChatId=$chatId"
                            )
                        }

                        else -> {
                            requestMarkAsRead("mensagem recebida no chat ativo")
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            repository.statusUpdates.collect { update ->
                val normalizedStatus = normalizeStatus(update.status)
                android.util.Log.d(
                    "ChatVM",
                    "MessageStatusUpdated recebido na UI: ${update.messageId} -> $normalizedStatus"
                )
                updateMessageStatus(update.messageId, normalizedStatus)
            }
        }
    }

    private fun updateMessageStatus(messageId: String, status: String) {
        var messageFound = false

        _uiState.update { state ->
            state.copy(
                messages = state.messages.map { message ->
                    if (message.id == messageId) {
                        messageFound = true
                        message.copy(status = status)
                    } else {
                        message
                    }
                }
            )
        }

        android.util.Log.d("ChatVM", "Status atualizado no estado: messageId=$messageId, found=$messageFound")
        if (!messageFound) {
            pendingStatusUpdates[messageId] = status
            android.util.Log.d("ChatVM", "Status pendente armazenado: messageId=$messageId, status=$status")
        }
    }

    private fun normalizeStatus(status: String): String {
        return when (status.trim().lowercase()) {
            "read", "lido" -> "Lido"
            "delivered", "entregue" -> "Entregue"
            "sent", "enviado" -> "Enviado"
            "failed", "falha" -> "Falha"
            else -> status
        }
    }

    private fun connectSignalR() {
        repository.connect(chatId)
    }

    fun onScreenActive() {
        isChatScreenActive = true
        android.util.Log.d("ChatVM", "ChatScreen ativa: chatId=$chatId")
        requestMarkAsRead("ChatScreen aberta")
    }

    fun onScreenInactive() {
        isChatScreenActive = false
        markAsReadRetryJob?.cancel()
        markAsReadRetryJob = null
        android.util.Log.d("ChatVM", "ChatScreen inativa: chatId=$chatId")
    }

    private fun requestMarkAsRead(reason: String) {
        if (!isChatScreenActive) {
            android.util.Log.d("ChatVM", "MarkAsRead ignorado porque ChatScreen nao esta ativa")
            return
        }

        android.util.Log.d(
            "ChatVM",
            "MarkAsRead chamado porque ChatScreen esta ativa: chatId=$chatId reason=$reason"
        )

        val sent = repository.markAsRead(chatId)
        if (sent) {
            markAsReadRetryJob?.cancel()
            markAsReadRetryJob = null
            return
        }

        scheduleMarkAsReadRetry()
    }

    private fun scheduleMarkAsReadRetry() {
        if (markAsReadRetryJob?.isActive == true) return

        markAsReadRetryJob = viewModelScope.launch {
            repeat(MARK_AS_READ_RETRY_COUNT) {
                delay(MARK_AS_READ_RETRY_DELAY_MS)
                if (!isChatScreenActive) {
                    android.util.Log.d("ChatVM", "MarkAsRead ignorado porque ChatScreen nao esta ativa")
                    return@launch
                }

                android.util.Log.d(
                    "ChatVM",
                    "Tentando MarkAsRead novamente porque ChatScreen esta ativa: chatId=$chatId"
                )

                if (repository.markAsRead(chatId)) {
                    markAsReadRetryJob = null
                    return@launch
                }
            }
        }
    }

    fun sendMessage(text: String) {
        val trimmedText = text.trim()
        if (trimmedText.isBlank()) return

        val optimisticMessage = ChatMessage(
            id = "$OPTIMISTIC_MESSAGE_PREFIX${System.currentTimeMillis()}",
            chatId = chatId,
            senderId = currentUserId,
            text = trimmedText,
            sentAt = Instant.now().toString(),
            status = "Enviado"
        )

        _uiState.update { state ->
            state.copy(messages = state.messages + optimisticMessage)
        }

        val sent = repository.sendMessage(chatId, trimmedText)
        if (!sent) {
            _uiState.update { state ->
                state.copy(
                    messages = state.messages.map { message ->
                        if (message.id == optimisticMessage.id) {
                            message.copy(status = "Falha")
                        } else {
                            message
                        }
                    }
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnect(chatId)
    }

    fun isMe(senderId: String) = senderId == currentUserId

    companion object {
        private const val OPTIMISTIC_MESSAGE_PREFIX = "local-"
        private const val MARK_AS_READ_RETRY_COUNT = 10
        private const val MARK_AS_READ_RETRY_DELAY_MS = 500L
    }
}
