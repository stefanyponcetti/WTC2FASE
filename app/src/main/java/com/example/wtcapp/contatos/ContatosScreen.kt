package com.example.wtcapp.contatos

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wtcapp.topbar.TopBar

@Composable
fun ContactItem(name: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(name)

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(Color(0xFFFF9800))
        )
    }
}

@Composable
fun ContatosScreen(
    onNavigateToChats: () -> Unit,
    onNavigateToPerfil: () -> Unit,
    onNavigateToComunicados: () -> Unit,
    onNavigateToContatos: () -> Unit
) {

    var isInternal by remember { mutableStateOf(true) }
    var selectedDepartment by remember { mutableStateOf("Marketing") }

    val departments = listOf("Marketing", "Vendas", "RH", "TI")

    val internalContacts = listOf(
        "Deborah Menezes",
        "Dinei Maurício de Souza",
        "Ellen Capellari",
        "Henrique Custódio",
        "Liber Giacomini Silva",
        "Mariana Ordella Fernandes",
        "Sabrina Moraes Vieira"
    )

    val externalContacts = listOf(
        "Cliente A",
        "Cliente B",
        "Fornecedor X",
        "Parceiro Y"
    )

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
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // 🔵 Toggle Interno/Externo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.LightGray)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { _, dragAmount ->
                            if (dragAmount > 0) isInternal = true
                            else if (dragAmount < 0) isInternal = false
                        }
                    }
                    .padding(4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isInternal) "INTERNO" else "EXTERNO",
                    modifier = Modifier
                        .background(Color(0xFF1976D2), RoundedCornerShape(20.dp))
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔽 Dropdown
            var expanded by remember { mutableStateOf(false) }

            Box {
                OutlinedButton(onClick = { expanded = true }) {
                    Text(selectedDepartment)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    departments.forEach { dept ->
                        DropdownMenuItem(
                            text = { Text(dept) },
                            onClick = {
                                selectedDepartment = dept
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 📋 Lista
            val contacts = if (isInternal) internalContacts else externalContacts

            LazyColumn {
                items(contacts) { name ->
                    ContactItem(name)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DirectoryScreenPreview() {
    MaterialTheme {
        ContatosScreen({}, {}, {}, {})
    }
}