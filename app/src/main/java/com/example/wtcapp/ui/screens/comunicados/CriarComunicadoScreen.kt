package com.example.wtcapp.ui.screens.comunicados

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wtcapp.ui.theme.azulFundo
import com.example.wtcapp.ui.theme.cinzaCard
import com.example.wtcapp.ui.theme.laranja
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 🎨 Paleta de cores oficial
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarComunicadoScreen(onBack: () -> Unit) {
    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Geral") }
    var destinatarios by remember { mutableStateOf("Todos") }
    var dataValidade by remember { mutableStateOf("") }
    var expandedCategoria by remember { mutableStateOf(false) }
    var expandedDestinatarios by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var imagemAnexada by remember { mutableStateOf(false) }

    val categorias = listOf("Geral", "Aviso Importante", "Evento", "Manutenção")
    val opcoesDestinatarios = listOf("Todos", "Gestores", "Colaboradores", "Participantes", "Visitantes")

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Criar Comunicado", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = cinzaCard)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = azulFundo
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ícone e título
            Icon(
                imageVector = Icons.Default.Campaign,
                contentDescription = null,
                tint = laranja,
                modifier = Modifier
                    .size(80.dp)
                    .padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text("Novo Comunicado", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Text("Preencha as informações abaixo para publicar.", color = Color.LightGray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(24.dp))

            // Card principal
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cinzaCard)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Informações Gerais", color = Color.LightGray, fontWeight = FontWeight.SemiBold)

                    // Campo título
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = customFieldColors()
                    )

                    // Campo descrição
                    OutlinedTextField(
                        value = descricao,
                        onValueChange = { if (it.length <= 500) descricao = it },
                        label = { Text("Descrição") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp),
                        maxLines = 6,
                        colors = customFieldColors()
                    )

                    Text(
                        text = "${descricao.length}/500 caracteres",
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        modifier = Modifier.align(Alignment.End)
                    )

                    // Categoria
                    ExposedDropdownMenuBox(
                        expanded = expandedCategoria,
                        onExpandedChange = { expandedCategoria = !expandedCategoria }
                    ) {
                        OutlinedTextField(
                            value = categoria,
                            onValueChange = {},
                            label = { Text("Categoria") },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = customFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategoria,
                            onDismissRequest = { expandedCategoria = false }
                        ) {
                            categorias.forEach { opcao ->
                                DropdownMenuItem(
                                    text = { Text(opcao) },
                                    onClick = {
                                        categoria = opcao
                                        expandedCategoria = false
                                    }
                                )
                            }
                        }
                    }

                    // Destinatários
                    ExposedDropdownMenuBox(
                        expanded = expandedDestinatarios,
                        onExpandedChange = { expandedDestinatarios = !expandedDestinatarios }
                    ) {
                        OutlinedTextField(
                            value = destinatarios,
                            onValueChange = {},
                            label = { Text("Destinatários") },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDestinatarios) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            colors = customFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedDestinatarios,
                            onDismissRequest = { expandedDestinatarios = false }
                        ) {
                            opcoesDestinatarios.forEach { opcao ->
                                DropdownMenuItem(
                                    text = { Text(opcao) },
                                    onClick = {
                                        destinatarios = opcao
                                        expandedDestinatarios = false
                                    }
                                )
                            }
                        }
                    }

                    // Campo data
                    OutlinedTextField(
                        value = dataValidade,
                        onValueChange = { dataValidade = it },
                        label = { Text("Data de validade") },
                        placeholder = { Text("Ex: 30/10/2025") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = customFieldColors()
                    )

                    // Botão de upload (simulado)
                    OutlinedButton(
                        onClick = {
                            imagemAnexada = true
                            scope.launch {
                                snackbarHostState.showSnackbar("Imagem adicionada com sucesso!")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null, tint = laranja)
                        Spacer(Modifier.width(8.dp))
                        Text(if (imagemAnexada) "Imagem anexada" else "Adicionar imagem ou ícone")
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botão Lista de convidados
                Button(
                    onClick = { /* TODO: ação para lista de convidados */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    shape = RoundedCornerShape(13.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(58.dp)
                ) {
                    Text("Lista de convidados", color = Color.White, fontSize = 11.sp)
                }

                // Botão Publicar
                Button(
                    onClick = { /* TODO: ação para publicar comunicado */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001F9F)), // Azul escuro
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(53.dp)
                ) {
                    Text("Publicar", color = Color.White, fontSize = 13.sp)
                }

                // Botão Enviar remessa
                Button(
                    onClick = { /* TODO: ação para enviar remessa */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8000)), // Laranja
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(51.dp)
                ) {
                    Text("Enviar remessa", color = Color.White, fontSize = 13.sp)
                }
            }


            Spacer(modifier = Modifier.height(24.dp))

            // Botões inferiores
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Visualização prévia não implementada ainda.")
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = laranja)
                ) {
                    Text("Visualizar")
                }

                Button(
                    onClick = {
                        isLoading = true
                        scope.launch {
                            delay(1500)
                            snackbarHostState.showSnackbar("Comunicado publicado com sucesso!")
                            isLoading = false
                            onBack()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = laranja),
                    enabled = !isLoading && titulo.isNotBlank() && descricao.isNotBlank()
                ) {
                    if (isLoading)
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    else
                        Text("Publicar")
                }
            }

            Text(
                "Após publicar, o comunicado será visível para os usuários selecionados.",
                color = Color.LightGray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun customFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = laranja,
    unfocusedBorderColor = Color.LightGray,
    cursorColor = laranja,
    focusedLabelColor = laranja,
    unfocusedLabelColor = Color.LightGray,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White
)
