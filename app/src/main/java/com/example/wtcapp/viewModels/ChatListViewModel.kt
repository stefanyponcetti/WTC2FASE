package com.example.wtcapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wtcapp.data.models.ChatModel
import com.example.wtcapp.data.models.CreateGroupRequest
import com.example.wtcapp.data.models.UserModel
import com.example.wtcapp.data.remote.RetrofitClient
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatListUiState(
    val grupos: List<ChatModel> = emptyList(),
    val privadosInternos: List<ChatModel> = emptyList(),
    val privadosExternos: List<ChatModel> = emptyList(),
    val users: List<UserModel> = emptyList(),
    val userNames: Map<String, String> = emptyMap(),
    val userTypes: Map<String, String> = emptyMap(),
    val isLoading: Boolean = false,
    val isCreatingGroup: Boolean = false,
    val error: String? = null,
    val navigateToChatId: String? = null,
    val navigateToChatName: String? = null
)

class ChatListViewModel(
    private val jwtToken: String,
    private val currentUserId: String
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState = _uiState.asStateFlow()
    private var chatMembers: Map<String, List<String>> = emptyMap()

    init {
        loadChats()
    }

    private fun loadChats() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val token = "Bearer $jwtToken"
                val users = RetrofitClient.chatApi.getUsers(token)
                val chats = RetrofitClient.chatApi.getMyChats(token)
                val userNames = users.associate { user -> user.id to user.nome }
                val userTypes = users.associate { user -> user.id to user.tipoCliente }
                val fallbackMembersMap = loadMissingChatMembers(token, chats)

                fun membersOf(chat: ChatModel): List<String> {
                    return chat.members ?: fallbackMembersMap[chat.id].orEmpty()
                }

                fun isInternalPrivateChat(chat: ChatModel): Boolean {
                    val otherMembers = membersOf(chat).filter { userId -> userId != currentUserId }
                    if (otherMembers.isEmpty()) return true

                    return otherMembers.all { userId ->
                        userTypes[userId]?.lowercase() == "interno"
                    }
                }

                val grupos = chats.filter { chat -> chat.isGroup }
                val privados = chats.filter { chat -> !chat.isGroup }
                chatMembers = fallbackMembersMap

                _uiState.update {
                    it.copy(
                        grupos = grupos,
                        privadosInternos = privados.filter { chat -> isInternalPrivateChat(chat) },
                        privadosExternos = privados.filter { chat -> !isInternalPrivateChat(chat) },
                        users = users.filter { user -> user.id != currentUserId },
                        userNames = userNames,
                        userTypes = userTypes,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Erro ao carregar chats."
                    )
                }
            }
        }
    }

    private suspend fun loadMissingChatMembers(
        token: String,
        chats: List<ChatModel>
    ): Map<String, List<String>> = coroutineScope {
        chats
            .filter { chat -> chat.members == null }
            .map { chat ->
                async {
                    runCatching {
                        chat.id to RetrofitClient.chatApi.getChatMembers(token, chat.id)
                    }.getOrNull()
                }
            }
            .awaitAll()
            .filterNotNull()
            .toMap()
    }

    fun getChatDisplayName(chat: ChatModel): String {
        if (chat.isGroup) return chat.groupName ?: "Grupo"

        val members = chat.members ?: chatMembers[chat.id] ?: return "Chat privado"
        val otherId = members.firstOrNull { memberId -> memberId != currentUserId }
            ?: return "Chat privado"

        return _uiState.value.userNames[otherId] ?: "Chat privado"
    }

    fun createGroup(name: String, memberIds: List<String>) {
        val groupName = name.trim()
        if (groupName.isBlank()) {
            _uiState.update { it.copy(error = "Informe o nome do grupo.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingGroup = true, error = null) }

            try {
                val chat = RetrofitClient.chatApi.createGroupChat(
                    token = "Bearer $jwtToken",
                    body = CreateGroupRequest(
                        name = groupName,
                        members = memberIds.distinct()
                    )
                )

                _uiState.update {
                    it.copy(
                        isCreatingGroup = false,
                        navigateToChatId = chat.id,
                        navigateToChatName = chat.groupName ?: groupName
                    )
                }

                loadChats()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCreatingGroup = false,
                        error = e.message ?: "Erro ao criar grupo."
                    )
                }
            }
        }
    }

    fun retry() {
        loadChats()
    }

    fun onNavigated() {
        _uiState.update {
            it.copy(
                navigateToChatId = null,
                navigateToChatName = null
            )
        }
    }
}
