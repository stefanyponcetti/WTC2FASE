package com.example.wtcapp.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wtcapp.topbar.TopBar
import kotlinx.coroutines.launch

@Composable
fun PerfilScreen(
    nome: String,
    email: String,
    telefone: String,
    dataNascimento: String,
    cargo: String,
    tempoEmpresa: String,
    unidade: String,
    tipoUsuario: String,
    onLogout: () -> Unit,
    onNavigateToChats: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToComunicados: () -> Unit,
    onNavigateToContatos: () -> Unit
) {

    val scope = rememberCoroutineScope()

    var isEditing by remember { mutableStateOf(false) }

    var nomeState by remember { mutableStateOf(nome) }
    var emailState by remember { mutableStateOf(email) }
    var telefoneState by remember { mutableStateOf(telefone) }
    var dataState by remember { mutableStateOf(dataNascimento) }
    var unidadeState by remember { mutableStateOf(unidade) }

    var loading by remember { mutableStateOf(false) }
    var mensagem by remember { mutableStateOf("") }

    val azulFundo = Color(0xFF384B5B)
    val laranja = Color(0xFFF18A21)

    Scaffold(
        topBar = {
            TopBar(
                onNavigate = { screen ->
                    when (screen) {
                        "chats" -> onNavigateToChats()
                        "perfil" -> onNavigateToPerfil()
                        "comunicados" -> onNavigateToComunicados()
                        "contatos" -> onNavigateToContatos()
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(azulFundo)
                .padding(innerPadding)
                .padding(20.dp)
        ) {

            Text(
                text = "PERFIL",
                fontSize = 26.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileField(
                label = "Nome",
                value = nomeState,
                icon = Icons.Default.Person,
                editable = isEditing,
                onValueChange = { nomeState = it }
            )

            ProfileField(
                label = "Email",
                value = emailState,
                icon = Icons.Default.Email,
                editable = isEditing,
                onValueChange = { emailState = it }
            )

            ProfileField(
                label = "Telefone",
                value = telefoneState,
                icon = Icons.Default.Phone,
                editable = isEditing,
                onValueChange = { telefoneState = it }
            )

            ProfileField(
                label = "Nascimento",
                value = dataState,
                icon = Icons.Default.DateRange,
                editable = isEditing,
                onValueChange = { dataState = it }
            )

            ProfileField(
                label = "Cargo",
                value = cargo,
                icon = Icons.Default.Work,
                editable = false,
                readOnly = true
            )

            ProfileField(
                label = "Unidade",
                value = unidadeState,
                icon = Icons.Default.Place,
                editable = isEditing,
                onValueChange = { unidadeState = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // BOTÕES
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
                            scope.launch {
                                kotlinx.coroutines.delay(1000)
                                loading = false
                                mensagem = "Atualizado com sucesso!"
                                isEditing = false
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
                Text(mensagem, color = Color.Green)
            }
        }
    }
}

@Composable
fun ProfileField(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    editable: Boolean,
    onValueChange: (String) -> Unit = {},
    readOnly: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(Color(0xFF4A5A68), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFFF18A21))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, color = Color.White)
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (editable && !readOnly) {
            androidx.compose.foundation.text.BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(color = Color.White, fontSize = 16.sp)
            )
        } else {
            Text(
                text = value,
                color = if (readOnly) Color.Gray else Color.White,
                fontSize = 16.sp
            )
        }
    }
}