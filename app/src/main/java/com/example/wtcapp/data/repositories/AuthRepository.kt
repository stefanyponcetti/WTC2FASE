package com.example.wtcapp.data.repositories

import com.example.wtcapp.data.SessionManager
import com.example.wtcapp.data.models.LoginGoogleRequest
import com.example.wtcapp.data.models.LoginRequest
import com.example.wtcapp.data.remote.RetrofitClient

data class AuthResult(
    val success: Boolean,
    val message: String
)

class AuthRepository(private val sessionManager: SessionManager) {
    suspend fun login(email: String, senha: String): AuthResult {
        val response = RetrofitClient.api.login(LoginRequest(email, senha))
        if (!response.isSuccessful) {
            return AuthResult(false, response.body()?.message ?: "Credenciais invalidas.")
        }

        val body = response.body()
        val token = body?.token
        if (token.isNullOrBlank()) {
            return AuthResult(false, body?.message ?: "Token nao retornado pelo servidor.")
        }

        val userId = body.idUsuario
            ?: body.userId
            ?: body.id
            ?: SessionManager.extractUserIdFromJwt(token)

        if (userId.isNullOrBlank()) {
            return AuthResult(false, "Nao foi possivel identificar o usuario do token.")
        }

        sessionManager.saveSession(token, userId)
        return AuthResult(true, "Login realizado com sucesso.")
    }

    suspend fun loginGoogle(idToken: String): AuthResult {
        val response = RetrofitClient.api.loginGoogle(LoginGoogleRequest(idToken))
        if (!response.isSuccessful) {
            return AuthResult(false, response.body()?.message ?: "Erro no login Google.")
        }

        val body = response.body()
        val token = body?.token
        if (token.isNullOrBlank()) {
            return AuthResult(false, body?.message ?: "Token nao retornado pelo servidor.")
        }

        val userId = body.idUsuario
            ?: body.userId
            ?: body.id
            ?: SessionManager.extractUserIdFromJwt(token)

        if (userId.isNullOrBlank()) {
            return AuthResult(false, "Nao foi possivel identificar o usuario do token.")
        }

        sessionManager.saveSession(token, userId)
        return AuthResult(true, "Login realizado com sucesso.")
    }
}
