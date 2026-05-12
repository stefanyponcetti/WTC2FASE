package com.example.wtcapp.ui.screens.perfil

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wtcapp.data.models.UpdateUsuarioDto
import com.example.wtcapp.data.remote.RetrofitClient
import com.example.wtcapp.ui.theme.azulFundo
import com.example.wtcapp.ui.theme.laranja
import kotlinx.coroutines.launch
import java.util.Calendar

fun converterData(data: String): String? {
    return try {
        val partes = data.trim().split("/")
        if (partes.size == 3) "${partes[2]}-${partes[1]}-${partes[0]}T00:00:00"
        else null
    } catch (e: Exception) {
        null
    }
}

@Composable
fun PerfilScreen(
    idUsuario: String,
    jwtToken: String?,
    onLogout: () -> Unit,
    onNavigateToChats: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToComunicados: () -> Unit,
    onNavigateToContatos: () -> Unit
) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var isEditing by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var mensagem by remember { mutableStateOf("") }

    var nomeState by remember { mutableStateOf("") }
    var emailState by remember { mutableStateOf("") }
    var telefoneState by remember { mutableStateOf("") }
    var dataState by remember { mutableStateOf("") }
    var unidadeState by remember { mutableStateOf("") }
    var cargo by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    var notificationsEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(idUsuario) {
        try {
            val user = RetrofitClient.api.buscarUsuario(jwtToken ?: "",idUsuario)
            nomeState = user.nome ?: ""
            emailState = user.email ?: ""
            telefoneState = user.telefone ?: ""
            unidadeState = user.unidade ?: ""
            cargo = user.cargo ?: ""
            dataState = user.dataNascimento ?: ""
        } catch (e: Exception) {
            mensagem = "Erro ao carregar perfil"
        }
    }

    Scaffold { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(azulFundo)
                .padding(innerPadding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Text("PERFIL", fontSize = 26.sp, color = Color.White)

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF4A5A68))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Notifications, null, tint = laranja)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Notificações", color = Color.White, modifier = Modifier.weight(1f))
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ProfileField("Nome", nomeState, Icons.Default.Person, isEditing) { nomeState = it }
            ProfileField("Email", emailState, Icons.Default.Email, isEditing) { emailState = it }
            ProfileField("Telefone", telefoneState, Icons.Default.Phone, isEditing) { telefoneState = it }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .then(if (isEditing) Modifier.clickable { showDatePicker = true } else Modifier),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.DateRange, null, tint = laranja)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Nascimento", color = Color.Gray, fontSize = 12.sp)
                    Text(
                        text = if (dataState.isBlank()) "Selecionar data" else dataState,
                        color = if (isEditing) laranja else Color.White,
                        fontSize = 16.sp
                    )
                }
            }
            ProfileField("Cargo", cargo, Icons.Default.Work, isEditing) { cargo = it }
            ProfileField("Unidade", unidadeState, Icons.Default.Place, isEditing) { unidadeState = it }

            Spacer(modifier = Modifier.height(20.dp))

            if (!isEditing) {

                Button(
                    onClick = { isEditing = true },
                    colors = ButtonDefaults.buttonColors(containerColor = laranja)
                ) {
                    Text("Editar Perfil")
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onLogout,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Sair")
                }

            } else {

                Row {

                    Button(
                        onClick = {
                            loading = true
                            mensagem = ""

                            scope.launch {
                                try {
                                    val dto = UpdateUsuarioDto(
                                        id = idUsuario,
                                        nome = nomeState,
                                        email = emailState,
                                        telefone = telefoneState,
                                        dataNascimento = converterData(dataState),
                                        unidade = unidadeState,
                                        cargo = cargo
                                    )

                                    RetrofitClient.api.atualizarUsuario(jwtToken ?: "",idUsuario, dto)


                                    val atualizado = RetrofitClient.api.buscarUsuario(jwtToken ?: "",idUsuario)

                                    nomeState = atualizado.nome ?: ""
                                    emailState = atualizado.email ?: ""
                                    telefoneState = atualizado.telefone ?: ""
                                    unidadeState = atualizado.unidade ?: ""
                                    cargo = atualizado.cargo ?: ""
                                    dataState = atualizado.dataNascimento ?: ""

                                    mensagem = "Atualizado com sucesso!"
                                    isEditing = false

                                } catch (e: Exception) {
                                    mensagem = "Erro: ${e.message}"
                                } finally {
                                    loading = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = laranja)
                    ) {
                        Text("Salvar")
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(onClick = { isEditing = false }) {
                        Text("Cancelar")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (loading) {
                CircularProgressIndicator(color = laranja)
            }

            if (mensagem.isNotEmpty()) {
                Text(
                    text = mensagem,
                    color = if (mensagem.startsWith("Erro")) Color.Red else Color.Green
                )
            }
        }
    }

    if (showDatePicker) {
        val calendar = Calendar.getInstance()

        LaunchedEffect(Unit) {
            DatePickerDialog(
                context,
                { _, year, month, day ->
                    dataState = "%02d/%02d/%04d".format(day, month + 1, year)
                    showDatePicker = false
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                setOnDismissListener { showDatePicker = false }
            }.show()
        }
    }
}

@Composable
fun ProfileField(
    label: String,
    value: String,
    icon: ImageVector,
    editable: Boolean,
    readOnly: Boolean = false,
    onValueChange: (String) -> Unit = {}
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(icon, null, tint = laranja)
        Spacer(modifier = Modifier.width(8.dp))

        if (editable && !readOnly) {

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label, color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.White),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = laranja,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = laranja
                )
            )

        } else {

            Column {
                Text(label, color = Color.Gray, fontSize = 12.sp)
                Text(value, color = Color.White, fontSize = 16.sp)
            }
        }
    }
}
