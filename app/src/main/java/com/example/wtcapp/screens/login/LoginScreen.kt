package com.example.wtcapp.screens.login

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.wtcapp.login.ForgotPasswordRequest
import com.example.wtcapp.login.LoginGoogleRequest
import com.example.wtcapp.login.LoginRequest
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

    val azulFundo = Color(0xFF384B5B)
    val laranjaBotao = Color(0xFFF18A21)

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
            // Título
            Text(
                text = "LOGIN",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Olá! Que bom ter você por aqui.\nVamos juntos criar estratégias que transformam!",
                color = Color(0xFFDDE5EC),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo de E-mail
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

            // Campo de Senha
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

            // Mensagem de erro
            if (errorMessage.isNotEmpty()) {
                FirebaseCrashlytics.getInstance().log(errorMessage)
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            val scope = rememberCoroutineScope()
            // Botão principal
            Button(
                onClick = {
                    if (email.isNotBlank() && senha.isNotBlank()) {
                        errorMessage = ""

                        scope.launch {
                            try {
                                val response = RetrofitClient.api.login(LoginRequest(email, senha))

                                if (response.isSuccessful && response.message() == "OK") {
                                    onLoginSuccess()
                                } else {
                                    errorMessage = response.body()?.message ?: "Credenciais inválidas."
                                    FirebaseCrashlytics.getInstance().recordException(RuntimeException(errorMessage))
                                }

                            } catch (e: Exception) {
                                    errorMessage = "Erro ao conectar ao servidor."
                                    FirebaseCrashlytics.getInstance().recordException(RuntimeException(e))

                            }
                        }

                    } else {
                        errorMessage = "Por favor, preencha e-mail e senha."
                        FirebaseCrashlytics.getInstance().recordException(RuntimeException(errorMessage))
                    }


                },
                colors = ButtonDefaults.buttonColors(containerColor = laranjaBotao),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Continuar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Textos clicáveis
            val context1 = LocalContext.current

// Localize o bloco da label "Esqueci minha senha" no seu arquivo LoginScreen.kt e substitua:

            Text(
                text = "Esqueci minha senha",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.clickable {
                    if (email.isBlank()) {
                        Toast.makeText(context1, "Preencha o e-mail para recuperar a senha", Toast.LENGTH_SHORT).show()
                    } else {
                        // CHAMADA AO BACKEND
                        scope.launch {
                            try {
                                val response = RetrofitClient.api2.forgotPassword(
                                    ForgotPasswordRequest(email)
                                )
                                if (response.isSuccess) {
                                    Toast.makeText(
                                        context1,
                                        "Se o e-mail existir, um token foi enviado.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(context1, "Erro ao solicitar recuperação.", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context1, "Erro de conexão.", Toast.LENGTH_SHORT).show()
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
                    text = "Já recebi o token para redefinição",
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
            val context = LocalContext.current
            val googleAuthManager = remember { GoogleAuthManager(context) }
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
                            errorMessage = "Token do Google inválido"
                            return@rememberLauncherForActivityResult
                        }

                        scope.launch {
                            try {
                                val response = RetrofitClient.api.loginGoogle(
                                    LoginGoogleRequest(idToken)
                                )

                                if (response.isSuccessful && response.message() == "OK") {
                                    onLoginSuccess()
                                } else {
                                    errorMessage = response.body()?.message
                                        ?: ("Erro no login Google" + idToken)
                                    FirebaseCrashlytics.getInstance().recordException(RuntimeException(errorMessage))
                                }

                            } catch (e: Exception) {
                                errorMessage = "Erro ao conectar com servidor" +e.message
                                FirebaseCrashlytics.getInstance().recordException(RuntimeException(errorMessage))
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

