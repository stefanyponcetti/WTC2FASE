package com.example.wtcapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wtcapp.data.models.ComunicadoModel
import com.example.wtcapp.data.models.CreateComunicadoRequest
import com.example.wtcapp.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ComunicadosUiState(
    val comunicados: List<ComunicadoModel> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val savedSuccess: Boolean = false
)

class ComunicadosViewModel(private val jwtToken: String) : ViewModel() {
    private val _uiState = MutableStateFlow(ComunicadosUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadComunicados()
    }

    fun loadComunicados() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val comunicados = RetrofitClient.chatApi.getComunicados("Bearer $jwtToken")
                _uiState.update {
                    it.copy(
                        comunicados = comunicados,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Erro ao carregar comunicados."
                    )
                }
            }
        }
    }

    fun createComunicado(
        titulo: String,
        descricao: String,
        categoria: String,
        destinatarios: String,
        dataValidade: String
    ) {
        if (titulo.isBlank() || descricao.isBlank() || dataValidade.isBlank()) {
            _uiState.update { it.copy(error = "Preencha título, descrição e data de validade.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null, savedSuccess = false) }

            try {
                RetrofitClient.chatApi.createComunicado(
                    token = "Bearer $jwtToken",
                    body = CreateComunicadoRequest(
                        titulo = titulo.trim(),
                        descricao = descricao.trim(),
                        categoria = categoria,
                        destinatarios = destinatarios,
                        dataValidade = dataValidade.trim()
                    )
                )

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        savedSuccess = true,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = e.message ?: "Erro ao criar comunicado."
                    )
                }
            }
        }
    }

    fun deleteComunicado(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }

            try {
                RetrofitClient.chatApi.deleteComunicado("Bearer $jwtToken", id)
                loadComunicados()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = e.message ?: "Erro ao excluir comunicado.")
                }
            }
        }
    }

    fun updateComunicado(
        id: String,
        titulo: String,
        descricao: String,
        categoria: String,
        destinatarios: String,
        dataValidade: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }

            try {
                RetrofitClient.chatApi.updateComunicado(
                    token = "Bearer $jwtToken",
                    id = id,
                    body = CreateComunicadoRequest(
                        titulo = titulo.trim(),
                        descricao = descricao.trim(),
                        categoria = categoria,
                        destinatarios = destinatarios,
                        dataValidade = dataValidade.trim()
                    )
                )

                _uiState.update { it.copy(isSaving = false) }
                loadComunicados()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = e.message ?: "Erro ao editar comunicado."
                    )
                }
            }
        }
    }

    fun retry() {
        loadComunicados()
    }

    fun onSavedHandled() {
        _uiState.update { it.copy(savedSuccess = false) }
    }
}
