package com.example.wtcapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wtcapp.data.models.UserModel
import com.example.wtcapp.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ContatosUiState(
    val internos: List<UserModel> = emptyList(),
    val externos: List<UserModel> = emptyList(),
    val cargos: List<String> = emptyList(),
    val selectedCargo: String = "Todos",
    val isLoading: Boolean = false,
    val error: String? = null,
    val navigateToChatId: String? = null,
    val navigateToChatName: String? = null
)

class ContatosViewModel(
    private val jwtToken: String,
    private val currentUserId: String
) : ViewModel() {
    private val _uiState = MutableStateFlow(ContatosUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val users = RetrofitClient.chatApi.getUsers("Bearer $jwtToken")
                    .filter { user -> user.id != currentUserId }
                val cargos = runCatching {
                    RetrofitClient.chatApi.getCargos("Bearer $jwtToken")
                }.getOrDefault(emptyList())
                val internos = users.filter { user -> user.tipoCliente.trim().lowercase() == "interno" }
                val externos = users.filter { user -> user.tipoCliente.trim().lowercase() != "interno" }
                val cargosDisponiveis = (cargos + users.mapNotNull { user -> user.cargo })
                    .map { cargo -> cargo.trim() }
                    .filter { cargo -> cargo.isNotBlank() }
                    .distinctBy { cargo -> cargo.lowercase() }
                    .sortedWith(String.CASE_INSENSITIVE_ORDER)

                _uiState.update {
                    it.copy(
                        internos = internos,
                        externos = externos,
                        cargos = listOf("Todos") + cargosDisponiveis,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Erro ao carregar contatos."
                    )
                }
            }
        }
    }

    fun onCargoSelected(cargo: String) {
        _uiState.update { it.copy(selectedCargo = cargo) }
    }

    fun startPrivateChat(targetUserId: String, targetName: String) {
        if (targetUserId == currentUserId) {
            _uiState.update { it.copy(error = "Nao e possivel criar chat consigo mesmo.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val chat = RetrofitClient.chatApi.createPrivateChat(
                    token = "Bearer $jwtToken",
                    targetUserId = targetUserId
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        navigateToChatId = chat.id,
                        navigateToChatName = targetName
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Erro ao iniciar chat privado."
                    )
                }
            }
        }
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
