package com.example.wtcapp.mensagem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import coil.compose.AsyncImage
import com.example.wtcapp.contatos.ContatosScreen
import com.example.wtcapp.topbar.TopBar
import com.example.wtcapp.R


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
            "Estamos convidando lideranças do setor de compras para o evento “AI in procurement”. " +
                    "Será uma tarde com palestras e dinâmicas com o objetivo de demonstrar como a IA pode ajudar " +
                    "na otimização da rotina diária de compras e gerar novas oportunidades. Pedimos que confirme " +
                    "vossa presença através do link abaixo de credenciamento."
        )

        MessageImageBubble(
            imageUrl = "https://www.linkpicture.com/q/ia-compras.jpg", // substitua por URL real se quiser
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
fun MessageImageBubble(imageUrl: String, link: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .background(Color(0xFFF4CBA0), shape = RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = "Imagem no Box",
            contentScale = ContentScale.Crop, // mantém proporção e corta excesso
            modifier = Modifier
                .fillMaxWidth()   // ocupa toda a largura disponível
                .height(150.dp)   // altura fixa para não distorcer
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(Modifier.height(6.dp))

        Text(link, color = Color(0xFF0645AD), fontSize = 14.sp)
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
        Icon(Icons.Default.PhotoCamera, contentDescription = "Câmera", tint = Color.White)
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
                onNavigateToChats,
                onNavigateToPerfil,
                onNavigateToComunicados,
                onNavigateToContatos
            )
        },
        bottomBar = {
            BottomBar()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // garante que o conteúdo não fique por baixo da TopBar/BottomBar
        ) {
            Spacer(Modifier.height(12.dp))
            MessageList()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DirectoryScreenPreview() {
    MaterialTheme {
        MensagemScreen({},{},{},{})
    }
}



