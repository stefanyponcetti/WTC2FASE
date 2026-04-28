package com.example.wtcapp.screens.chats

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesomeMotion
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wtcapp.R
import com.example.wtcapp.screens.topbar.TopBar

data class ChatItem(
    val chatId : String,
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
fun ChatSection(title: String, chats: List<ChatItem>, onNavigateToNovaMensagem: (String, String) -> Unit) {
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
                style = MaterialTheme.typography.titleMedium
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Recolher" else "Expandir"
            )
        }

        Divider()

        AnimatedVisibility(visible = isExpanded) {
            LazyColumn {
                items(chats) { chat ->
                    ChatRow(chat,onNavigateToNovaMensagem)
                }
            }
        }
    }
}

@Composable
fun ChatRow(chat: ChatItem, onNavigateToNovaMensagem: (String, String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onNavigateToNovaMensagem(chat.chatId, chat.name)
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        // Avatar placeholder
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Gray, shape = CircleShape)
        ){

            Image(
                painter = painterResource(id = chat.image),
                contentDescription = "Imagem no Box",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = chat.name, style = MaterialTheme.typography.bodyMedium)
            Text(text = chat.role, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(text = chat.message, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        if (chat.pin) {
            Spacer(modifier = Modifier.height(4.dp))
            Icon(
                imageVector = Icons.Default.PushPin,
                contentDescription = "Pin",
                tint = Color(0xFFFF9800),
                modifier = Modifier.size(16.dp)
            )
        }

        if (chat.note) {
            Spacer(modifier = Modifier.height(4.dp))
            Icon(
                imageVector = Icons.Default.AutoAwesomeMotion,
                contentDescription = "Note",
                tint = Color.Yellow,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(text = chat.time, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}
@Composable
fun ChatListScreen(onNavigateToChats: () -> Unit,
                   onNavigateToPerfil: () -> Unit,
                   onNavigateToComunicados: () -> Unit,
                   onNavigateToContatos: () -> Unit,
                   onNavigateToNovaMensagem: (String, String) -> Unit) {
    val internos = listOf(
        ChatItem(
            "juliana_id",
            "Juliana Vilaboa",
            "Analista BI PL",
            "Juliana: Atualizei o Dashboard de recrutamento até quinta-fe...",
            "9h31",
            R.drawable.juliana,
            true,
            false
        ),
        ChatItem(
            "ronaldo_id",
            "Ronaldo Matarazzo",
            "Coordenador de projetos",
            "Ronaldo: Temos mais de 300 mídias, então já se enquadraria no for...",
            "Ontem",
            R.drawable.ronaldo,
            false,
            true
        ),
        ChatItem(
            "stephanie_id",
            "Stephanie",
            "CFO People and culture",
            "Você: este case may be presented by the IT team next week, what would be...",
            "21/10",
            R.drawable.stephanie,
            false,
            true
        )
    )

    val externos = listOf(
        ChatItem(
            "julia_id",
            "Julia Silveira",
            "CFO People and culture",
            "Ok, façamos assim então.",
            "Ontem",
            R.drawable.julia,
            false,
            false
        ),
        ChatItem(
            "sarah_id",
            "Sarah Vibe",
            "Internal Nowast LTDA",
            "Você: Sarah, não localizei o contrato da Nowast no portal, você pode me...",
            "18/10",
            R.drawable.sarah,
            false,
            false
        ),
        ChatItem(
            "felipe_seri",
            "Felipe Seri",
            "Gerente - Vicenzzo LTDA",
            "Felipe: Sem problemas, mas mandamos devido à agenda Eliane.",
            "17/10",
            R.drawable.felipe,
            false,
            false
        )
    )


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
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(innerPadding)
            ) {
                ChatSection("INTERNOS", internos, onNavigateToNovaMensagem)
                ChatSection("EXTERNOS", externos, onNavigateToNovaMensagem)
            }
        }
        FloatingActionButton(
            onClick = onNavigateToContatos,
            containerColor = laranja,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Novo chat", tint = Color.White)
        }

    }


}

