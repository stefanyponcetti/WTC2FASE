package com.example.wtcapp.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val cinzaCard = Color(0xFF4A5A68)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(onNavigateToChats: () -> Unit,
           onNavigateToPerfil: () -> Unit,
           onNavigateToComunicados: () ->
           Unit,onNavigateToContatos: () -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(cinzaCard)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onNavigateToPerfil) {
            Icon(Icons.Default.AccountCircle, contentDescription = "perfil", tint = Color.White)
        }
        IconButton(onClick = onNavigateToComunicados) {
            Icon(Icons.Default.Notifications, contentDescription = "Notificações", tint = Color(0xFFFF9800))
        }
        IconButton(onClick = onNavigateToChats) {
            Icon(Icons.Default.Message, contentDescription = "Chat", tint = Color.White)
        }
        IconButton(onClick = onNavigateToContatos) {
            Icon(Icons.Default.Phone, contentDescription = "Telefone",tint = Color.White)
        }
        IconButton(onClick = onNavigateToContatos) {
            Icon(Icons.Default.MoreVert, contentDescription = "Configurações", tint = Color.White)
        }
    }
}