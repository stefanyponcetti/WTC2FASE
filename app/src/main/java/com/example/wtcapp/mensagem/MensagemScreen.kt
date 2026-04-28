package com.example.wtcapp.mensagem

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wtcapp.R
import com.example.wtcapp.topbar.TopBar

val cinzaCard = Color(0xFF4A5A68)

@Composable
fun MessageList() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MessageBubble("Bom dia, prezada Fernanda.")

        MessageBubble(
            "Estamos convidando lideranças do setor de compras para o evento AI in procurement. " +
                    "Será uma tarde com palestras e dinâmicas sobre IA aplicada às compras."
        )

        MessageImageBubble(
            link = "https://wtceventscenter.com.br/eventsnovprocurement123"
        )
    }
}

@Composable
fun MessageBubble(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .background(Color(0xFFF4CBA0), shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(text, fontSize = 15.sp, color = Color.Black)
    }
}

@Composable
fun MessageImageBubble(link: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .background(Color(0xFFF4CBA0), shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = link,
            color = Color(0xFF0645AD),
            fontSize = 14.sp
        )
    }
}

@Composable
fun BottomBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(cinzaCard)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Digite uma mensagem...",
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        )

        Icon(
            imageVector = Icons.Default.PhotoCamera,
            contentDescription = "Câmera",
            tint = Color.White
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MensagemScreen(
    onNavigateToChats: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToComunicados: () -> Unit,
    onNavigateToContatos: () -> Unit
) {

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
        bottomBar = {
            BottomBar()
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            MessageList()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MensagemPreview() {
    MaterialTheme {
        MensagemScreen({}, {}, {}, {})
    }
}