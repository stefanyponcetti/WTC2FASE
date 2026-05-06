package com.example.wtcapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wtcapp.data.models.ChatMessage
import com.example.wtcapp.data.repositories.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
                _uiState.update { it.copy(messages = history, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun collectMessages() {
        viewModelScope.launch {
            repository.incomingMessages.collect { newMessage ->
                android.util.Log.d("SignalR", "ViewModel recebeu: ${newMessage.text}")
                _uiState.update { state ->
                    state.copy(messages = state.messages + newMessage)
                }
            }
        }
    }


    private fun connectSignalR() {
        repository.connect(chatId)
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        repository.sendMessage(chatId, text)
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnect(chatId)
    }

    fun isMe(senderId: String) = senderId == currentUserId
}
