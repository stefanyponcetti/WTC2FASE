package com.example.wtcapp.screens.topbar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TopBar(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        IconButton(onClick = { onNavigate("chats") }) {
            Icon(Icons.Default.Message, contentDescription = null, tint = Color.White)
        }

        IconButton(onClick = { onNavigate("comunicados") }) {
            Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
        }

        IconButton(onClick = { onNavigate("perfil") }) {
            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.White)
        }

        IconButton(onClick = { onNavigate("contatos") }) {
            Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White)
        }
    }
}