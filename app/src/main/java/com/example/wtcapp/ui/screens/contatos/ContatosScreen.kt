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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wtcapp.data.models.UserModel
import com.example.wtcapp.ui.components.TopBar
import com.example.wtcapp.ui.theme.azulFundo
import com.example.wtcapp.ui.theme.cinzaCard
import com.example.wtcapp.ui.theme.laranja
import com.example.wtcapp.viewmodels.ContatosUiState
import com.example.wtcapp.viewmodels.ContatosViewModel

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
            onInternalChanged = { isInternal = it },
            onContactClick = { user ->
                viewModel.startPrivateChat(user.id, user.nome)
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

@Composable
private fun ContatosContent(
    uiState: ContatosUiState,
    isInternal: Boolean,
    onInternalChanged: (Boolean) -> Unit,
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

        Spacer(modifier = Modifier.height(16.dp))

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
                val contacts = if (isInternal) uiState.internos else uiState.externos

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
