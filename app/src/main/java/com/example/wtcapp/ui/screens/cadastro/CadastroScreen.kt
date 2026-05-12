package com.example.wtcapp.ui.screens.cadastro

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wtcapp.data.models.RegisterRequest
import com.example.wtcapp.data.remote.RetrofitClient
import com.example.wtcapp.ui.components.DatePickerField
import com.example.wtcapp.ui.components.converterDataParaApi
import com.example.wtcapp.ui.theme.azulFundo
import com.example.wtcapp.ui.theme.laranja
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CadastroScreen(onNavigateToLogin: () -> Unit, onCadastroSuccess: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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

            TextButton(onClick = onNavigateToLogin) {
                Text("Ja possui cadastro? Clique aqui.", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (isClient) "Externo" else "Interno",
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
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            CadastroTextField(
                value = nome,
                onValueChange = { nome = it },
                label = "Nome completo"
            )

            Spacer(modifier = Modifier.height(16.dp))

            CadastroTextField(
                value = email,
                onValueChange = { email = it },
                label = "E-mail"
            )

            Spacer(modifier = Modifier.height(16.dp))

            CadastroTextField(
                value = senha,
                onValueChange = { senha = it },
                label = "Senha",
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            DatePickerField(
                label = "Data de nascimento",
                value = dataNascimento,
                onDateSelected = { dataNascimento = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (email.isBlank() || senha.isBlank() || nome.isBlank() || dataNascimento.isBlank()) {
                        Toast.makeText(context, "Preencha todos os campos obrigatorios.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val dominioEmpresa = "wtc.com.br"
                    val ehOperador = !isClient

                    if (ehOperador && !email.endsWith("@$dominioEmpresa")) {
                        Toast.makeText(context, "Apenas e-mails corporativos podem se cadastrar como operador.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val dataNascimentoApi = converterDataParaApi(dataNascimento)
                    if (dataNascimentoApi.isNullOrBlank()) {
                        Toast.makeText(context, "Selecione uma data de nascimento valida.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val tipoUsuario = if (isClient) "externo" else "interno"
                    scope.launch {
                        try {
                            val response = RetrofitClient.api.register(
                                RegisterRequest(tipoUsuario, nome, email, senha, dataNascimentoApi)
                            )

                            if (!response.isSuccessful) {
                                Toast.makeText(context, "Erro no servidor: ${response.message()}", Toast.LENGTH_LONG).show()
                                return@launch
                            }

                            auth.createUserWithEmailAndPassword(email, senha)
                                .addOnSuccessListener { result ->
                                    val userId = result.user?.uid ?: return@addOnSuccessListener
                                    val userMap = hashMapOf(
                                        "nome" to nome,
                                        "email" to email,
                                        "dataNascimento" to dataNascimento
                                    )

                                    db.collection("users").document(userId)
                                        .set(userMap)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Cadastro concluido!", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(context, "Erro ao salvar dados: ${e.message}", Toast.LENGTH_LONG).show()
                                            FirebaseCrashlytics.getInstance().recordException(RuntimeException(e))
                                        }

                                    onCadastroSuccess()
                                    Toast.makeText(context, "Cadastro concluido!", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Erro no Firebase: ${e.message}", Toast.LENGTH_LONG).show()
                                    FirebaseCrashlytics.getInstance().recordException(RuntimeException(e))
                                }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Erro ao conectar ao servidor.", Toast.LENGTH_LONG).show()
                            FirebaseCrashlytics.getInstance().recordException(RuntimeException(e))
                        }
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

@Composable
private fun CadastroTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = visualTransformation,
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
}
