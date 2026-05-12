package com.example.wtcapp.ui.screens.comunicados

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wtcapp.data.models.ComunicadoModel
import com.example.wtcapp.ui.components.DatePickerField
import com.example.wtcapp.ui.components.TopBar
import com.example.wtcapp.ui.components.converterDataParaApiDateOnly
import com.example.wtcapp.ui.components.formatarDataParaExibicao
import com.example.wtcapp.ui.theme.azulFundo
import com.example.wtcapp.ui.theme.cinzaCard
import com.example.wtcapp.ui.theme.laranja
import com.example.wtcapp.viewmodels.ComunicadosUiState
import com.example.wtcapp.viewmodels.ComunicadosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComunicadosScreen(
    jwtToken: String,
    tipoCliente: String,
    onNavigateToCriar: () -> Unit,
    onNavigateToChats: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToComunicados: () -> Unit,
    onNavigateToContatos: () -> Unit
) {
    val viewModel = remember { ComunicadosViewModel(jwtToken) }
    val uiState by viewModel.uiState.collectAsState(initial = ComunicadosUiState())
    val azulGradiente = Brush.verticalGradient(
        colors = listOf(Color(0xFF3F5466), Color(0xFF2E3D4A))
    )
    val tipoClienteNormalizado = tipoCliente.trim().lowercase()
    val canManageComunicados = tipoClienteNormalizado == "interno"
    val comunicadosVisiveis = uiState.comunicados.filter { comunicado ->
        podeVisualizarComunicado(comunicado.destinatarios, tipoClienteNormalizado)
    }
    var editandoComunicado by remember { mutableStateOf<ComunicadoModel?>(null) }

    Scaffold(
        topBar = {
            TopBar(
                onNavigate = { destino ->
                    when (destino) {
                        "chats" -> onNavigateToChats()
                        "perfil" -> onNavigateToPerfil()
                        "comunicados" -> onNavigateToComunicados()
                        "contatos" -> onNavigateToContatos()
                    }
                }
            )
        },
        floatingActionButton = {
            if (canManageComunicados) {
                FloatingActionButton(
                    onClick = onNavigateToCriar,
                    containerColor = laranja,
                    shape = CircleShape,
                    modifier = Modifier.shadow(8.dp, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Novo Comunicado",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        containerColor = azulFundo
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(azulGradiente)
        ) {
            Text(
                text = "Últimos comunicados",
                color = Color(0xFFDDE5EC),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp)
            )

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = laranja)
                    }
                }

                uiState.error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = uiState.error.orEmpty(),
                                color = Color.White,
                                fontSize = 15.sp
                            )
                            Button(onClick = { viewModel.retry() }) {
                                Text("Tentar novamente")
                            }
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(
                            items = comunicadosVisiveis,
                            key = { comunicado -> comunicado.id }
                        ) { comunicado ->
                            val canManageItem = canManageComunicados && comunicado.podeEditar
                            AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                                ComunicadoItem(
                                    comunicado = comunicado,
                                    canManage = canManageItem,
                                    onDelete = {
                                        if (canManageItem) {
                                            viewModel.deleteComunicado(comunicado.id)
                                        }
                                    },
                                    onEdit = { editandoComunicado = it }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    editandoComunicado?.let { comunicado ->
        if (canManageComunicados && comunicado.podeEditar) {
            EditarComunicadoDialog(
                comunicado = comunicado,
                onDismiss = { editandoComunicado = null },
                onSave = { titulo, descricao, categoria, destinatarios, dataValidade ->
                    viewModel.updateComunicado(
                        id = comunicado.id,
                        titulo = titulo,
                        descricao = descricao,
                        categoria = categoria,
                        destinatarios = destinatarios,
                        dataValidade = dataValidade
                    )
                    editandoComunicado = null
                },
                isSaving = uiState.isSaving
            )
        }
    }
}

private fun podeVisualizarComunicado(destinatarios: String?, tipoCliente: String): Boolean {
    val dest = destinatarios?.trim()?.lowercase()
    val tipo = tipoCliente.trim().lowercase()

    return dest == "todos" || dest == tipo
}

@Composable
private fun ComunicadoItem(
    comunicado: ComunicadoModel,
    canManage: Boolean,
    onDelete: () -> Unit,
    onEdit: (ComunicadoModel) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(14.dp))
            .then(
                if (canManage) Modifier.clickable { onEdit(comunicado) }
                else Modifier
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cinzaCard)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    comunicado.titulo,
                    color = laranja,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    modifier = Modifier.weight(1f)
                )
                if (canManage) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir",
                            tint = Color(0xFFB3261E)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                comunicado.descricao,
                color = Color.White,
                fontSize = 15.sp,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "Por: ${comunicado.autor}",
                color = Color(0xFFDDE5EC),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditarComunicadoDialog(
    comunicado: ComunicadoModel,
    onDismiss: () -> Unit,
    onSave: (
        titulo: String,
        descricao: String,
        categoria: String,
        destinatarios: String,
        dataValidade: String
    ) -> Unit,
    isSaving: Boolean
) {
    var titulo by remember { mutableStateOf(comunicado.titulo) }
    var descricao by remember { mutableStateOf(comunicado.descricao) }
    var categoria by remember { mutableStateOf(comunicado.categoria) }
    var destinatarios by remember { mutableStateOf(comunicado.destinatarios) }
    var dataValidade by remember { mutableStateOf(formatarDataParaExibicao(comunicado.dataValidade)) }
    var expandedCategoria by remember { mutableStateOf(false) }
    var expandedDestinatarios by remember { mutableStateOf(false) }

    val categorias = listOf("Geral", "Aviso Importante", "Evento", "Manutenção")
    val opcoesDestinatarios = listOf("Todos", "Interno", "Externo")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = cinzaCard,
        title = {
            Text("Editar Comunicado", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4,
                    colors = customFieldColors()
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
                DatePickerField(
                    label = "Data de validade",
                    value = dataValidade,
                    onDateSelected = { dataValidade = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val dataValidadeApi = converterDataParaApiDateOnly(dataValidade) ?: return@Button
                    onSave(titulo, descricao, categoria, destinatarios, dataValidadeApi)
                },
                colors = ButtonDefaults.buttonColors(containerColor = laranja),
                enabled = !isSaving &&
                    titulo.isNotBlank() &&
                    descricao.isNotBlank() &&
                    converterDataParaApiDateOnly(dataValidade) != null
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Text("Salvar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.White)
            }
        }
    )
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

@Preview(showBackground = true)
@Composable
fun DirectoryScreenPreview() {
    MaterialTheme {
        ComunicadosScreen("token", "interno", {}, {}, {}, {}, {})
    }
}
