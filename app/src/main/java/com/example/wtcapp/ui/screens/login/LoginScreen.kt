package com.example.wtcapp.ui.screens.login

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wtcapp.auth.GoogleAuthManager
import com.example.wtcapp.data.SessionManager
import com.example.wtcapp.data.models.ForgotPasswordRequest
import com.example.wtcapp.data.remote.RetrofitClient
import com.example.wtcapp.data.repositories.AuthRepository
import com.example.wtcapp.ui.theme.azulFundo
import com.example.wtcapp.ui.theme.laranja
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onNavigateToCadastro: () -> Unit,
    onLoginSuccess: () -> Unit,
    onNavigateToRedefinirSenha: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sessionManager = remember(context) { SessionManager(context.applicationContext) }
    val authRepository = remember(sessionManager) { AuthRepository(sessionManager) }
    val googleAuthManager = remember(context) { GoogleAuthManager(context) }
    val googleSignInClient = googleAuthManager.getClient()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken

                if (idToken == null) {
                    errorMessage = "Token do Google invalido"
                    return@rememberLauncherForActivityResult
                }

                scope.launch {
                    try {
                        val loginResult = authRepository.loginGoogle(idToken)
                        if (loginResult.success) {
                            onLoginSuccess()
                        } else {
                            errorMessage = loginResult.message
                            FirebaseCrashlytics.getInstance()
                                .recordException(RuntimeException(errorMessage))
                        }
                    } catch (e: Exception) {
                        errorMessage = "Erro ao conectar com servidor: ${e.message}"
                        FirebaseCrashlytics.getInstance()
                            .recordException(RuntimeException(errorMessage))
                    }
                }
            } catch (e: ApiException) {
                errorMessage = "Erro Google: ${e.statusCode}"
                FirebaseCrashlytics.getInstance().recordException(RuntimeException(errorMessage))
            }
        } else {
            errorMessage = "Login cancelado ou falhou"
            FirebaseCrashlytics.getInstance().recordException(RuntimeException(errorMessage))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(azulFundo, azulFundo.copy(alpha = 0.95f))
                )
            )
            .padding(horizontal = 32.dp, vertical = 48.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "LOGIN",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ola! Que bom ter voce por aqui.\nVamos juntos criar estrategias que transformam!",
                color = Color(0xFFDDE5EC),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-MAIL", color = Color.White) },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null, tint = Color.White)
                },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color(0xFFB0BEC5),
                    cursorColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text("SENHA", color = Color.White) },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White)
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color(0xFFB0BEC5),
                    cursorColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (errorMessage.isNotEmpty()) {
                FirebaseCrashlytics.getInstance().log(errorMessage)
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    if (email.isBlank() || senha.isBlank()) {
                        errorMessage = "Por favor, preencha e-mail e senha."
                        FirebaseCrashlytics.getInstance()
                            .recordException(RuntimeException(errorMessage))
                        return@Button
                    }

                    errorMessage = ""
                    scope.launch {
                        try {
                            val loginResult = authRepository.login(email, senha)
                            if (loginResult.success) {
                                onLoginSuccess()
                            } else {
                                errorMessage = loginResult.message
                                FirebaseCrashlytics.getInstance()
                                    .recordException(RuntimeException(errorMessage))
                            }
                        } catch (e: Exception) {
                            errorMessage = "Erro ao conectar ao servidor."
                            FirebaseCrashlytics.getInstance().recordException(RuntimeException(e))
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = laranja),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Continuar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Esqueci minha senha",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.clickable {
                    if (email.isBlank()) {
                        Toast.makeText(
                            context,
                            "Preencha o e-mail para recuperar a senha",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        scope.launch {
                            try {
                                val response = RetrofitClient.passwordApi.forgotPassword(
                                    ForgotPasswordRequest(email)
                                )
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "Se o e-mail existir, um token foi enviado.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Erro ao solicitar recuperacao.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Erro de conexao.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            )

            TextButton(
                onClick = { onNavigateToRedefinirSenha() },
                modifier = Modifier.padding(top = 0.dp)
            ) {
                Text(
                    text = "Ja recebi o token para redefinicao",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Fazer cadastro",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.clickable { onNavigateToCadastro() }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "OU",
                color = Color(0xFFDDE5EC),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = {
                    val signInIntent = googleSignInClient.signInIntent
                    launcher.launch(signInIntent)
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp,
                    brush = Brush.linearGradient(listOf(Color.White, Color.White))
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Continuar com o Google", fontSize = 14.sp)
            }
        }
    }
}
