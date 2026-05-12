package com.example.wtcapp.ui.screens.contatos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wtcapp.data.models.UpsertNoteRequest
import com.example.wtcapp.data.models.UserModel
import com.example.wtcapp.data.remote.RetrofitClient
import com.example.wtcapp.ui.components.TopBar
import com.example.wtcapp.ui.theme.azulFundo
import com.example.wtcapp.ui.theme.cinzaCard
import com.example.wtcapp.ui.theme.laranja
import com.example.wtcapp.viewmodels.ContatosUiState
import com.example.wtcapp.viewmodels.ContatosViewModel
import kotlinx.coroutines.launch

@Composable
fun ContactItem(
    user: UserModel,
    onClick: () -> Unit
) {
    val initial = user.nome.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(cinzaCard),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.nome,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = user.email,
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(laranja)
        )
    }
}

@Composable
fun ContatosScreen(
    jwtToken: String,
    currentUserId: String,
    onNavigateToChats: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToComunicados: () -> Unit,
    onNavigateToContatos: () -> Unit,
    onNavigateToChat: (chatId: String, chatName: String) -> Unit
) {
    val viewModel = remember { ContatosViewModel(jwtToken, currentUserId) }
    val uiState by viewModel.uiState.collectAsState(initial = ContatosUiState())

    var isInternal by remember { mutableStateOf(true) }
    var selectedContact by remember { mutableStateOf<UserModel?>(null) }

    LaunchedEffect(uiState.navigateToChatId, uiState.navigateToChatName) {
        val chatId = uiState.navigateToChatId
        val chatName = uiState.navigateToChatName

        if (!chatId.isNullOrBlank() && !chatName.isNullOrBlank()) {
            onNavigateToChat(chatId, chatName)
            viewModel.onNavigated()
        }
    }

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
        },
        containerColor = azulFundo
    ) { innerPadding ->
        ContatosContent(
            uiState = uiState,
            isInternal = isInternal,
            onInternalChanged = {
                isInternal = it
                viewModel.onCargoSelected("Todos")
            },
            onCargoSelected = viewModel::onCargoSelected,
            onContactClick = { user -> selectedContact = user },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }

    selectedContact?.let { user ->
        ContactDetailDialog(
            user = user,
            jwtToken = jwtToken,
            onDismiss = { selectedContact = null },
            onStartChat = {
                selectedContact = null
                viewModel.startPrivateChat(user.id, user.nome)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContatosContent(
    uiState: ContatosUiState,
    isInternal: Boolean,
    onInternalChanged: (Boolean) -> Unit,
    onCargoSelected: (String) -> Unit,
    onContactClick: (UserModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(cinzaCard)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        if (dragAmount > 0) onInternalChanged(true)
                        else if (dragAmount < 0) onInternalChanged(false)
                    }
                }
                .padding(4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isInternal) "INTERNO" else "EXTERNO",
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(laranja)
                    .clickable { onInternalChanged(!isInternal) }
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        var expandedCargo by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expandedCargo,
            onExpandedChange = { expandedCargo = !expandedCargo },
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = uiState.selectedCargo,
                onValueChange = {},
                label = { Text("Cargo", color = Color.White) },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCargo)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = laranja,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = laranja,
                    unfocusedLabelColor = Color.Gray
                )
            )
            ExposedDropdownMenu(
                expanded = expandedCargo,
                onDismissRequest = { expandedCargo = false }
            ) {
                uiState.cargos.forEach { cargo ->
                    DropdownMenuItem(
                        text = { Text(cargo) },
                        onClick = {
                            onCargoSelected(cargo)
                            expandedCargo = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

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
                    Text(
                        text = uiState.error,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            else -> {
                val baseContacts = if (isInternal) uiState.internos else uiState.externos
                val contacts = baseContacts.filter { user ->
                    cargoMatches(user.cargo, uiState.selectedCargo)
                }

                if (contacts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhum contato encontrado.",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    LazyColumn {
                        items(contacts, key = { user -> user.id }) { user ->
                            ContactItem(
                                user = user,
                                onClick = { onContactClick(user) }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun cargoMatches(userCargo: String?, selectedCargo: String): Boolean {
    return selectedCargo.trim().equals("Todos", ignoreCase = true) ||
        userCargo?.trim()?.equals(selectedCargo.trim(), ignoreCase = true) == true
}

@Composable
private fun ContactDetailDialog(
    user: UserModel,
    jwtToken: String,
    onDismiss: () -> Unit,
    onStartChat: () -> Unit
) {
    var note by remember { mutableStateOf("") }
    var isLoadingNote by remember { mutableStateOf(true) }
    var isSavingNote by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(user.id) {
        try {
            val result = RetrofitClient.chatApi.getContactNote(
                "Bearer $jwtToken",
                user.id
            )
            note = result.note
        } catch (_: Exception) {
        }
        isLoadingNote = false
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = cinzaCard,
        title = {
            Column {
                Text(
                    user.nome,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    user.email,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
                if (!user.cargo.isNullOrBlank()) {
                    Text(
                        user.cargo,
                        color = laranja,
                        fontSize = 13.sp
                    )
                }
            }
        },
        text = {
            Column {
                Text(
                    "Anotação privada",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (isLoadingNote) {
                    CircularProgressIndicator(
                        color = laranja,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        placeholder = { Text("Escreva uma anotação...", color = Color.Gray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 80.dp),
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = laranja,
                            unfocusedBorderColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = laranja
                        )
                    )
                }
            }
        },
        confirmButton = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        scope.launch {
                            isSavingNote = true
                            try {
                                RetrofitClient.chatApi.upsertContactNote(
                                    "Bearer $jwtToken",
                                    user.id,
                                    UpsertNoteRequest(note)
                                )
                            } catch (_: Exception) {
                            }
                            isSavingNote = false
                            onStartChat()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = laranja),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Iniciar conversa")
                }
                TextButton(
                    onClick = {
                        scope.launch {
                            isSavingNote = true
                            try {
                                RetrofitClient.chatApi.upsertContactNote(
                                    "Bearer $jwtToken",
                                    user.id,
                                    UpsertNoteRequest(note)
                                )
                            } catch (_: Exception) {
                            }
                            isSavingNote = false
                            onDismiss()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (isSavingNote) "Salvando..." else "Salvar anotação",
                        color = Color.White
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar", color = Color.White.copy(alpha = 0.6f))
            }
        }
    )
}
