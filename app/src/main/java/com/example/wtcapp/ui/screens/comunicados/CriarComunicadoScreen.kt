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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.wtcapp.viewmodels.ComunicadosUiState
import com.example.wtcapp.viewmodels.ComunicadosViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarComunicadoScreen(
    jwtToken: String,
    onBack: () -> Unit
) {
    val viewModel = remember { ComunicadosViewModel(jwtToken) }
    val uiState by viewModel.uiState.collectAsState(initial = ComunicadosUiState())

    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Geral") }
    var destinatarios by remember { mutableStateOf("Todos") }
    var dataValidade by remember { mutableStateOf("") }
    var expandedCategoria by remember { mutableStateOf(false) }
    var expandedDestinatarios by remember { mutableStateOf(false) }

    val categorias = listOf("Geral", "Aviso Importante", "Evento", "Manutenção")
    val opcoesDestinatarios = listOf("Todos", "Interno", "Externo")
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.savedSuccess) {
        if (uiState.savedSuccess) {
            viewModel.onSavedHandled()
            onBack()
        }
    }

    LaunchedEffect(uiState.error) {
        val error = uiState.error
        if (!error.isNullOrBlank()) {
            snackbarHostState.showSnackbar(error)
        }
    }

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

                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = customFieldColors()
                    )

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

                    OutlinedTextField(
                        value = dataValidade,
                        onValueChange = { dataValidade = it },
                        label = { Text("Data de validade") },
                        placeholder = { Text("yyyy-MM-dd") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = customFieldColors()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = laranja)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = {
                        scope.launch {
                            viewModel.createComunicado(
                                titulo = titulo,
                                descricao = descricao,
                                categoria = categoria,
                                destinatarios = destinatarios,
                                dataValidade = dataValidade
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = laranja),
                    enabled = !uiState.isSaving && titulo.isNotBlank() && descricao.isNotBlank() && dataValidade.isNotBlank()
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    } else {
                        Text("Salvar")
                    }
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
