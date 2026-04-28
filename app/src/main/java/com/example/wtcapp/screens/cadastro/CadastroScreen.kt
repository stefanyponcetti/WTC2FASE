package com.example.wtcapp.screens.cadastro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun CadastroScreen(onNavigateToLogin: () -> Unit, onCadastroSuccess: () -> Unit) {
    val azulFundo = Color(0xFF384B5B)
    val laranja = Color(0xFFF18A21)
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val focusManager = LocalFocusManager.current
    val dominioEmpresa = "wtc.com.br"

    var isClient by remember { mutableStateOf(true) }
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var dataNascimento by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(azulFundo)
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "CADASTRO",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            TextButton(onClick = { onNavigateToLogin() }) {
                Text("Já possui cadastro? Clique aqui.", color = Color.White)
            }

            // Switch Cliente / Operador
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isClient) "Cliente" else "Operador",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )

                Spacer(modifier = Modifier.width(8.dp))

                Switch(
                    checked = isClient,
                    onCheckedChange = { isClient = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = laranja,
                        uncheckedThumbColor = azulFundo,
                        checkedTrackColor = azulFundo,
                        uncheckedTrackColor = laranja
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Campo: Nome completo
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome completo") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo: E-mail
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo: Senha
            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo: Data de nascimento
            OutlinedTextField(
                value = dataNascimento,
                onValueChange = { novoValor ->
                    // Remove tudo que não for número
                    var digits = novoValor.filter { it.isDigit() }

                    // Limita a 8 dígitos (ddMMyyyy)
                    if (digits.length > 8) digits = digits.take(8)

                    // Adiciona a formatação: dd/mm/aaaa
                    dataNascimento = when {
                        digits.length >= 5 -> digits.replaceFirst(
                            "(\\d{2})(\\d{2})(\\d+)"
                                .toRegex(),
                            "$1/$2/$3"
                        )
                        digits.length >= 3 -> digits.replaceFirst(
                            "(\\d{2})(\\d+)"
                                .toRegex(),
                            "$1/$2"
                        )
                        else -> digits
                    }
                },
                label = { Text("Data de nascimento") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next // Mostra “Próximo” no teclado
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        // Aqui você pode mudar o foco para o próximo campo
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botão de cadastro
            Button(
                onClick = {
                    if (email.isNotBlank() && senha.isNotBlank() && nome.isNotBlank()) {
                        val dominioEmpresa = "wtc.com.br"
                        val ehOperador = !isClient

                        // Verifica o domínio se for operador
                        if (ehOperador && !email.endsWith("@$dominioEmpresa")) {
                            println("Apenas e-mails corporativos podem se cadastrar como operador.")
                            return@Button
                        }

                        // Cria o usuário no Firebase Auth
                        auth.createUserWithEmailAndPassword(email, senha)
                            .addOnSuccessListener { result ->
                                val userId = result.user?.uid
                                val tipoUsuario = if (ehOperador) "operador" else "cliente"

                                val userMap = hashMapOf(
                                    "nome" to nome,
                                    "email" to email,
                                    "dataNascimento" to dataNascimento,
                                    "tipo" to tipoUsuario
                                )

                                db.collection("users").document(userId!!).set(userMap)
                                    .addOnSuccessListener {
                                        println("Usuário $tipoUsuario cadastrado com sucesso!")
                                        onCadastroSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        println("Erro ao salvar dados: ${e.message}")
                                    }
                            }
                            .addOnFailureListener { e ->
                                println("Erro ao criar conta: ${e.message}")
                            }
                    } else {
                        println("Preencha todos os campos obrigatórios.")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = laranja),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Inscrever-se", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}
