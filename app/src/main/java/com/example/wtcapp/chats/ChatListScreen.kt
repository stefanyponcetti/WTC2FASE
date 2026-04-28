package com.example.wtcapp.chats

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.wtcapp.R
import com.example.wtcapp.topbar.TopBar

data class ChatItem(
    val name: String,
    val role: String,
    val message: String,
    val time: String,
    val image: Int,
    val pin: Boolean,
    val note: Boolean
)

val laranja = Color(0xFFF18A21)

@Composable
fun ChatSection(
    title: String,
    chats: List<ChatItem>,
    onNavigateToNovaMensagem: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null
            )
        }

        Divider()

        if (isExpanded) {
            LazyColumn {
                items(chats) { chat ->
                    ChatRow(chat, onNavigateToNovaMensagem)
                }
            }
        }
    }
}

@Composable
fun ChatRow(chat: ChatItem, onNavigateToNovaMensagem: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToNovaMensagem() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = chat.image),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(chat.name)
            Text(chat.role, color = Color.Gray)
            Text(
                chat.message,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (chat.pin) {
            Icon(Icons.Default.PushPin, contentDescription = null, tint = Color(0xFFFF9800))
        }

        if (chat.note) {
            Icon(Icons.Default.AutoAwesomeMotion, contentDescription = null, tint = Color.Yellow)
        }

        Text(chat.time, color = Color.Gray)
    }
}

@Composable
fun ChatListScreen(
    onNavigateToChats: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToComunicados: () -> Unit,
    onNavigateToContatos: () -> Unit,
    onNavigateToNovaMensagem: () -> Unit
) {

    val internos = listOf(
        ChatItem("Juliana Vilaboa", "Analista BI PL", "Mensagem...", "9h31", R.drawable.juliana, true, false),
        ChatItem("Ronaldo Matarazzo", "Coordenador", "Mensagem...", "Ontem", R.drawable.ronaldo, false, true)
    )

    val externos = listOf(
        ChatItem("Julia Silveira", "CFO", "Mensagem...", "Ontem", R.drawable.julia, false, false),
        ChatItem("Sarah Vibe", "Empresa", "Mensagem...", "18/10", R.drawable.sarah, false, false)
    )

    Box(modifier = Modifier.fillMaxSize()) {

        Column {
            ChatSection("INTERNOS", internos, onNavigateToNovaMensagem)
            ChatSection("EXTERNOS", externos, onNavigateToNovaMensagem)
        }

        TopBar(
            onNavigate = { screen ->
                when (screen) {
                    "chats" -> onNavigateToChats()
                    "perfil" -> onNavigateToPerfil()
                    "comunicados" -> onNavigateToComunicados()
                    "contatos" -> onNavigateToContatos()
                }
            },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        FloatingActionButton(
            onClick = onNavigateToContatos,
            containerColor = laranja,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
        }
    }
}