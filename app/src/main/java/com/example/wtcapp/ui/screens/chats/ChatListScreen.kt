package com.example.wtcapp.ui.screens.chats

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.wtcapp.data.models.ChatModel
import com.example.wtcapp.data.models.UserModel
import com.example.wtcapp.ui.components.TopBar
import com.example.wtcapp.ui.theme.azulFundo
import com.example.wtcapp.ui.theme.cinzaCard
import com.example.wtcapp.ui.theme.laranja
import com.example.wtcapp.viewmodels.ChatListUiState
import com.example.wtcapp.viewmodels.ChatListViewModel

@Composable
fun ChatSection(
    title: String,
    chats: List<ChatModel>,
    viewModel: ChatListViewModel,
    currentUserId: String,
    onNavigateToNovaMensagem: (String, String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Recolher" else "Expandir",
                tint = Color.White
            )
        }

        HorizontalDivider(color = Color.White.copy(alpha = 0.16f))

        AnimatedVisibility(visible = isExpanded) {
            if (chats.isEmpty()) {
                Text(
                    text = "Nenhuma conversa ainda",
                    color = Color.White.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            } else {
                LazyColumn {
                    items(chats, key = { chat -> chat.id }) { chat ->
                        ChatRow(
                            chat = chat,
                            chatName = viewModel.getChatDisplayName(chat),
                            currentUserId = currentUserId,
                            onNavigateToNovaMensagem = onNavigateToNovaMensagem
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatRow(
    chat: ChatModel,
    chatName: String,
    currentUserId: String,
    onNavigateToNovaMensagem: (String, String) -> Unit
) {
    val initial = chatName.firstOrNull()?.uppercaseChar()?.toString() ?: "C"
    val lastMessageText = when {
        chat.lastMessage == null -> "Nenhuma mensagem ainda"
        chat.lastMessage.senderId == currentUserId -> "Voce: ${chat.lastMessage.text}"
        else -> chat.lastMessage.text
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onNavigateToNovaMensagem(chat.id, chatName)
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(cinzaCard, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initial,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = chatName,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = lastMessageText,
                color = Color.White.copy(alpha = 0.65f),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (chat.isGroup) {
            Icon(
                imageVector = Icons.Default.Group,
                contentDescription = "Grupo",
                tint = laranja,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ChatListScreen(
    onNavigateToChats: () -> Unit,
    jwtToken: String,
    currentUserId: String,
    onNavigateToPerfil: () -> Unit,
    onNavigateToComunicados: () -> Unit,
    onNavigateToContatos: () -> Unit,
    onNavigateToNovaMensagem: (String, String) -> Unit
) {
    val viewModel = remember { ChatListViewModel(jwtToken, currentUserId) }
    val uiState by viewModel.uiState.collectAsState(initial = ChatListUiState())
    var showCreateOptions by remember { mutableStateOf(false) }
    var showGroupDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.navigateToChatId, uiState.navigateToChatName) {
        val chatId = uiState.navigateToChatId
        val chatName = uiState.navigateToChatName
        if (!chatId.isNullOrBlank() && !chatName.isNullOrBlank()) {
            onNavigateToNovaMensagem(chatId, chatName)
            viewModel.onNavigated()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
            containerColor = azulFundo
        ) { innerPadding ->
            ChatListContent(
                uiState = uiState,
                viewModel = viewModel,
                currentUserId = currentUserId,
                onRetry = viewModel::retry,
                onNavigateToNovaMensagem = onNavigateToNovaMensagem,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }

        FloatingActionButton(
            onClick = { showCreateOptions = true },
            containerColor = laranja,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Novo chat", tint = Color.White)
        }
    }

    if (showCreateOptions) {
        AlertDialog(
            onDismissRequest = { showCreateOptions = false },
            title = { Text("Novo chat") },
            text = { Text("Escolha como deseja iniciar a conversa.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCreateOptions = false
                        showGroupDialog = true
                    }
                ) {
                    Text("Grupo")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCreateOptions = false
                        onNavigateToContatos()
                    }
                ) {
                    Text("Privado")
                }
            }
        )
    }

    if (showGroupDialog) {
        CreateGroupDialog(
            users = uiState.users,
            isCreating = uiState.isCreatingGroup,
            onDismiss = { showGroupDialog = false },
            onCreateGroup = { name, memberIds ->
                viewModel.createGroup(name, memberIds)
                showGroupDialog = false
            }
        )
    }
}

@Composable
private fun ChatListContent(
    uiState: ChatListUiState,
    viewModel: ChatListViewModel,
    currentUserId: String,
    onRetry: () -> Unit,
    onNavigateToNovaMensagem: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        uiState.isLoading -> {
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = laranja)
            }
        }

        uiState.error != null -> {
            Column(
                modifier = modifier.padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = uiState.error,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.size(16.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = laranja)
                ) {
                    Text("Tentar novamente")
                }
            }
        }

        else -> {
            Column(modifier = modifier) {
                ChatSection(
                    title = "GRUPOS",
                    chats = uiState.grupos,
                    viewModel = viewModel,
                    currentUserId = currentUserId,
                    onNavigateToNovaMensagem = onNavigateToNovaMensagem
                )
                ChatSection(
                    title = "INTERNOS",
                    chats = uiState.privadosInternos,
                    viewModel = viewModel,
                    currentUserId = currentUserId,
                    onNavigateToNovaMensagem = onNavigateToNovaMensagem
                )
                ChatSection(
                    title = "EXTERNOS",
                    chats = uiState.privadosExternos,
                    viewModel = viewModel,
                    currentUserId = currentUserId,
                    onNavigateToNovaMensagem = onNavigateToNovaMensagem
                )
            }
        }
    }
}

@Composable
private fun CreateGroupDialog(
    users: List<UserModel>,
    isCreating: Boolean,
    onDismiss: () -> Unit,
    onCreateGroup: (String, List<String>) -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    val selectedIds = remember { mutableStateListOf<String>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Criar grupo") },
        text = {
            Column {
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Nome do grupo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                LazyColumn(modifier = Modifier.heightIn(max = 260.dp)) {
                    items(users, key = { user -> user.id }) { user ->
                        val checked = user.id in selectedIds

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (checked) selectedIds.remove(user.id)
                                    else selectedIds.add(user.id)
                                }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { isChecked ->
                                    if (isChecked) selectedIds.add(user.id)
                                    else selectedIds.remove(user.id)
                                }
                            )
                            Column {
                                Text(user.nome, fontWeight = FontWeight.SemiBold)
                                Text(
                                    user.email,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isCreating && groupName.isNotBlank(),
                onClick = { onCreateGroup(groupName, selectedIds.toList()) },
                colors = ButtonDefaults.buttonColors(containerColor = laranja)
            ) {
                Text(if (isCreating) "Criando..." else "Criar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
