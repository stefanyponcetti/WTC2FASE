package com.example.wtcapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TopBar(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primary,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            IconButton(onClick = { onNavigate("chats") }) {
                Icon(
                    imageVector = Icons.Default.Message,
                    contentDescription = "Chats",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            IconButton(onClick = { onNavigate("comunicados") }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificações",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            IconButton(onClick = { onNavigate("perfil") }) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Perfil",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            IconButton(onClick = { onNavigate("contatos") }) {
                Icon(
                    imageVector = Icons.Default.EventNote,
                    contentDescription = "Contatos",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}